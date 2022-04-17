package com.spring.restapi.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.TokenDAO;
import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.encrypt.SHA;
import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;
import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.exception.user.UnableToUpdateCountsException;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;
import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.ExpiredJwtException;

@Service("tokenService")
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class
		}
)
public class TokenService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private TokenDAO tokenDAO;
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
		tokenDAO.updateTokens(param);
		
		//���ο� �׼��� �������� ��ū�� ��Ű�� ������ ����
		response.addCookie(cookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi",14*24*60*60));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/tokens",14*24*60*60));
		
		//���� �׼���, �������� ��ū ��Ȱ��ȭ
		redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		
		System.out.println("�׼��� �������� ��ū�� ��߱��߽��ϴ�.");
	}
	
	public void login(HashMap<String,String> param, HttpServletResponse response) throws InvalidIdException, InvalidPwException, Exception{

		//�ش� ID�� �ִ��� Ȯ��
		String user_id = tokenDAO.checkId(param);
		
		//��Ʈ�� ����
		String user_salt = tokenDAO.getSalt(param);
		
		//���Ű�� ����
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//���Ű�� �ؽ�
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//��й�ȣ ��ȿ�� �˻�
		user_pw = tokenDAO.checkPw(param);
		
		//�׼���, �������� ��ū �߱�
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		
		String user_accesstoken = jwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = jwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//�׼���, �������� ��ū DB�� ����
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		tokenDAO.updateTokens(param);
		
		//�׼���, �������� ��ū ��Ű�� ÷��
		response.addCookie(cookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi",14*24*60*60));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/tokens",14*24*60*60));
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
		
		HashMap param = new HashMap();
		param.put("user_id", jwtUtil.getData(user_accesstoken,"user_id"));
		tokenDAO.deleteTokens(param);
		
		//�׼���, �������� ��ū ��Ű�� ÷��
		response.addCookie(cookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken","removed","/restapi/tokens",0));
	}
	
	public HashMap getTokens(HashMap param){
		return tokenDAO.getTokens(param);
	}
}
