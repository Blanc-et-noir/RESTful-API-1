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

		//액세스 리프레시 토큰 재발급
		String user_id = jwtUtil.getData(user_accesstoken, "user_id");
		UserVO user = new UserVO();
		user.setUser_id(user_id);

		String new_user_accesstoken = jwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String new_user_refreshtoken = jwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//새로운 액세스 리프레시 토큰으로 DB 업데이트
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", new_user_accesstoken);
		param.put("user_refreshtoken", new_user_refreshtoken);
		userDAO.updateTokens(param);
		
		//새로운 액세스 리프레시 토큰을 쿠키에 저장후 응답
		response.addCookie(cookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi"));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/token"));
		
		//기존 액세스, 리프레시 토큰 비활성화
		redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		
		System.out.println("액세스 리프레시 토큰을 재발급했습니다.");
	}
	
	public void logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		try {
			redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		}catch(ExpiredJwtException e) {
			System.out.println("액세스토큰 유효기간이 지나 블랙리스트에 추가할 필요 X");
		}
		
		try {
			redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		}catch(ExpiredJwtException e) {
			System.out.println("리프레시토큰 유효기간이 지나 블랙리스트에 추가할 필요 X");
		}
		
		//액세스, 리프레쉬 토큰 쿠키에 첨부
		response.addCookie(cookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken","removed","/restapi/token",0));
	}
}
