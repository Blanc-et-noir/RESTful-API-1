package com.spring.restapi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.dao.UserDAO;
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
	private UserDAO userDAO;
	@Autowired
	private CookieUtil cookieUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		
		//액세스토큰이 없으면
		if(user_accesstoken==null) {
			System.out.println("액세스 토큰이 없습니다.");
			response.sendError(401, "액세스 토큰이 없습니다.");
			return false;
		}else {
			//로그아웃된 액세스토큰이면
			if(redisUtil.getData(user_accesstoken)!=null) {
				System.out.println("해당 액세스 토큰은 이미 로그아웃 처리되었습니다.");
				response.sendError(401, "해당 액세스 토큰은 이미 로그아웃 처리되었습니다.");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
				System.out.println("액세스 토큰이 정상입니다.");
				return true;
			}catch(ExpiredJwtException e) {
				System.out.println("액세스 토큰의 기한이 만료되었습니다.");
				response.sendError(401, "액세스 토큰의 기한이 만료되었습니다.");
				return false;
			}catch(Exception e) {
				System.out.println("액세스 토큰이 위조되었거나 잘못되었습니다.");
				response.sendError(401, "액세스 토큰이 위조되었거나 잘못되었습니다.");
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
