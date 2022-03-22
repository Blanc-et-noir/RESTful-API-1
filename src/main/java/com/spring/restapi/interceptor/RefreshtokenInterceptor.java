package com.spring.restapi.interceptor;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.dao.UserDAO;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;
import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.ExpiredJwtException;

public class RefreshtokenInterceptor implements HandlerInterceptor{
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
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		//액세스토큰이 없으면
		if(user_refreshtoken == null) {
			System.out.println("리프레시 토큰이 없습니다.");
			response.sendError(401, "리프레시 토큰이 없습니다.");
			return false;
		}else if(user_accesstoken==null) {
			System.out.println("액세스 토큰이 없습니다.");
			response.sendError(401, "액세스 토큰이 없습니다.");
			return false;
		}else {
			//로그아웃된 리프레시토큰이면
			if(redisUtil.getData(user_refreshtoken)!=null) {
				System.out.println("해당 리프레시 토큰은 이미 로그아웃 처리되었습니다.");
				response.sendError(401, "해당 리프레시 토큰은 이미 로그아웃 처리되었습니다.");
				return false;
			}
			
			//로그아웃된 액세스토큰이면
			if(redisUtil.getData(user_accesstoken)!=null) {
				System.out.println("해당 액세스 토큰은 이미 로그아웃 처리되었습니다.");
				response.sendError(401, "해당 액세스 토큰은 이미 로그아웃 처리되었습니다.");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
			}catch(ExpiredJwtException e) {
				System.out.println("액세스 토큰의 기한이 만료되었습니다.");
			}catch(Exception e) {
				System.out.println("액세스 토큰이 위조되었거나 잘못되었습니다.");
				response.sendError(401, "액세스 토큰이 위조되었거나 잘못되었습니다.");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_refreshtoken);
				
				//액세스와 리프레시 id가 같은지 확인
				String access_user_id = jwtUtil.getData(user_accesstoken, "user_id");
				String refresh_user_id = jwtUtil.getData(user_refreshtoken, "user_id");
				
				//현재 사용중이었던 액세스와 리프레시인지 확인
				if(!access_user_id.equals(refresh_user_id)) {
					System.out.println("액세스 토큰과 리프레시 토큰의 사용자 정보가 일치하지 않습니다.");
					response.sendError(401, "액세스 토큰과 리프레시 토큰의 사용자 정보가 일치하지 않습니다.");
					return false;
				}
				
				System.out.println("액세스 리프레시 토큰이 모두 정상입니다.");
				return true;
			}catch(ExpiredJwtException e) {
				System.out.println("리프레시 토큰의 기한이 만료되었습니다.");
				response.sendError(401, "리프레시 토큰의 기한이 만료되었습니다.");
				return false;
			}catch(Exception e) {
				System.out.println("리프레시 토큰이 위조되었거나 잘못되었습니다.");
				response.sendError(401, "리프레시 토큰이 위조되었거나 잘못되었습니다.");
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
