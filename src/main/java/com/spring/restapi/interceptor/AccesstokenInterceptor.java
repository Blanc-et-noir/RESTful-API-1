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
		
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String uri = request.getRequestURI();
		String method = request.getMethod();
		
		//�ű� ȸ�� ����, ȸ������ ��ȸ�� ������ ��� GET ��û�� ��ū�� �ʿ� ����
		if(uri.equals("/restapi/users")&&method.equals("POST")) {
			return true;
		}else if(!uri.equals("/restapi/users")&&method.equals("GET")) {
			return true;
		}
		
		//�׼��� ��ū ����
		if(user_accesstoken==null) {
			setErrorMessage(response,401,"�α��� ������ ��ȿ���� ����");
			return false;
		}else {
			//�α׾ƿ��� �׼��� ��ū
			if(RedisUtil.getData(user_accesstoken)!=null) {
				setErrorMessage(response,401,"�α��� ������ ��ȿ���� ����");
				return false;
			}
			
			try {
				JwtUtil.validateToken(user_accesstoken);
				return true;
			//�׼��� ��ū ��ȿ�Ⱓ ����
			}catch(ExpiredJwtException e) {
				setErrorMessage(response,401,"�α��� ������ ��ȿ���� ����");
				return false;
			//������ �׼��� ��ū
			}catch(Exception e) {
				setErrorMessage(response,401,"�α��� ������ ��ȿ���� ����");
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
