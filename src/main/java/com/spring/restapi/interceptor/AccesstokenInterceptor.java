package com.spring.restapi.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

import io.jsonwebtoken.ExpiredJwtException;



public class AccesstokenInterceptor implements HandlerInterceptor{
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private CookieUtil cookieUtil;
	
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
		
		String uri = request.getRequestURI();
		String method = request.getMethod();
		
		//아래의 동작들은 토큰 없이도 동작
		if(uri.equals("/restapi/tokens")&&method.equals("POST")) {
			return true;
		}else if(uri.equals("/restapi/articles")&&method.equals("GET")) {
			return true;
		}else if(uri.equals("/restapi/users")&&method.equals("POST")) {
			return true;
		}
		
		//액세스토큰이 없으면
		if(user_accesstoken==null) {
			response.sendError(401);
			return false;
		}else {
			//로그아웃된 액세스토큰이면
			if(redisUtil.getData(user_accesstoken)!=null) {
				setErrorMessage(response,401,"로그아웃된 액세스 토큰");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
				return true;
			}catch(ExpiredJwtException e) {
				setErrorMessage(response,401,"액세스 토큰 유효기간 만료");
				return false;
			}catch(Exception e) {
				setErrorMessage(response,401,"위조된 액세스 토큰");
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
