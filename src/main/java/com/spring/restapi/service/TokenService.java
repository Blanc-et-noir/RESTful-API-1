package com.spring.restapi.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.UserDAO;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;
import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.ExpiredJwtException;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class
		}
)
@Service("TokenService")
public class TokenService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CookieUtil cookieUtil;
	
	public void refreshTokens(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HashMap result = new HashMap();
		
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);

		//�׼��� �������� ��ū ��߱�
		String user_id = jwtUtil.getData(user_accesstoken, "user_id");
		UserVO user = new UserVO();
		user.setUser_id(user_id);

		String new_user_accesstoken = jwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String new_user_refreshtoken = jwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//���ο� �׼��� �������� ��ū���� DB ������Ʈ
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", new_user_accesstoken);
		param.put("user_refreshtoken", new_user_refreshtoken);
		userDAO.updateTokens(param);
		
		//���ο� �׼��� �������� ��ū�� ��Ű�� ������ ����
		response.addCookie(cookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi"));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/token"));
		
		//���� �׼���, �������� ��ū ��Ȱ��ȭ
		redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		
		System.out.println("�׼��� �������� ��ū�� ��߱��߽��ϴ�.");
	}
	
	public void logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		try {
			redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		}catch(ExpiredJwtException e) {
			System.out.println("�׼�����ū ��ȿ�Ⱓ�� ���� ������Ʈ�� �߰��� �ʿ� X");
		}
		
		try {
			redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		}catch(ExpiredJwtException e) {
			System.out.println("����������ū ��ȿ�Ⱓ�� ���� ������Ʈ�� �߰��� �ʿ� X");
		}
		
		//�׼���, �������� ��ū ��Ű�� ÷��
		response.addCookie(cookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken","removed","/restapi/token",0));
	}
}
