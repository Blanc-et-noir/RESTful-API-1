package com.spring.restapi.interceptor;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.service.TokenService;
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
	private TokenService tokenService;
	
	private void setErrorMessage(HttpServletResponse response, int errorcode, String message){
		try {
			JSONObject json = new JSONObject();
			json.put("flag", false);
			json.put("content", message);
			
			response.setStatus(401);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		String uri = request.getRequestURI();
		String method = request.getMethod();
		
		if(uri.equals("/restapi/tokens")&&method.equals("POST")) {
			return true;
		}
		
		//액세스토큰이 없으면
		if(user_refreshtoken == null) {
			response.sendError(401);
			return false;
		}else if(user_accesstoken==null) {
			response.sendError(401);
			return false;
		}else {
			//로그아웃된 리프레시토큰이면
			if(redisUtil.getData(user_refreshtoken)!=null) {
				setErrorMessage(response,401,"로그아웃된 리프레시 토큰");
				return false;
			}
			
			//로그아웃된 액세스토큰이면
			if(redisUtil.getData(user_accesstoken)!=null) {
				setErrorMessage(response,401,"로그아웃된 액세스 토큰");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
			}catch(ExpiredJwtException e) {

			}catch(Exception e) {
				setErrorMessage(response,401,"위조된 액세스 토큰");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_refreshtoken);
				
				//액세스와 리프레시 id가 같은지 확인
				String access_user_id = jwtUtil.getData(user_accesstoken, "user_id");
				String refresh_user_id = jwtUtil.getData(user_refreshtoken, "user_id");
				
				//현재 사용중이었던 액세스와 리프레시인지 확인
				if(!access_user_id.equals(refresh_user_id)) {
					setErrorMessage(response,401,"액세스 토큰, 리프레쉬 토큰의 소유자 ID 불일치");
					return false;
				}
				
				HashMap param = new HashMap();
				param.put("user_id", access_user_id);
				HashMap tokens = tokenService.getTokens(param);
				
				if(!user_accesstoken.equals(tokens.get("user_accesstoken"))||!user_refreshtoken.equals(tokens.get("user_refreshtoken"))) {
					setErrorMessage(response,401,"액세스 토큰 또는 리프레쉬 토큰이 로그인에 사용한 것이 아님");
					return false;
				}
				return true;
			}catch(ExpiredJwtException e) {
				setErrorMessage(response,401,"리프레쉬 토큰 유효기간 만료");
				return false;
			}catch(Exception e) {
				setErrorMessage(response,401,"위조된 리프레쉬 토큰");
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