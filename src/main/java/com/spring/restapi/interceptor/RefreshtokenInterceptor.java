package com.spring.restapi.interceptor;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.service.UserService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

import io.jsonwebtoken.ExpiredJwtException;

public class RefreshtokenInterceptor implements HandlerInterceptor{
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		//�׼�����ū�� ������
		if(user_refreshtoken == null) {
			response.sendError(401);
			return false;
		}else if(user_accesstoken==null) {
			response.sendError(401);
			return false;
		}else {
			//�α׾ƿ��� ����������ū�̸�
			if(redisUtil.getData(user_refreshtoken)!=null) {
				response.sendError(401);
				return false;
			}
			
			//�α׾ƿ��� �׼�����ū�̸�
			if(redisUtil.getData(user_accesstoken)!=null) {
				response.sendError(401);
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
			}catch(ExpiredJwtException e) {

			}catch(Exception e) {
				response.sendError(401);
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_refreshtoken);
				
				//�׼����� �������� id�� ������ Ȯ��
				String access_user_id = jwtUtil.getData(user_accesstoken, "user_id");
				String refresh_user_id = jwtUtil.getData(user_refreshtoken, "user_id");
				
				//���� ������̾��� �׼����� ������������ Ȯ��
				if(!access_user_id.equals(refresh_user_id)) {
					response.sendError(401);
					return false;
				}
				
				HashMap param = new HashMap();
				param.put("user_id", access_user_id);
				HashMap tokens = userService.getTokens(param);
				
				if(!user_accesstoken.equals(tokens.get("user_accesstoken"))||!user_refreshtoken.equals(tokens.get("user_refreshtoken"))) {
					response.sendError(401);
					return false;
				}
				return true;
			}catch(ExpiredJwtException e) {
				response.sendError(401);
				return false;
			}catch(Exception e) {
				response.sendError(401);
				return false;
			}
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub
		
	}
}