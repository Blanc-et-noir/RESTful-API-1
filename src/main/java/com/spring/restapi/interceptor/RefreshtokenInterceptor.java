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
		
		//�׼�����ū�� ������
		if(user_refreshtoken == null) {
			System.out.println("�������� ��ū�� �����ϴ�.");
			response.sendError(401, "�������� ��ū�� �����ϴ�.");
			return false;
		}else if(user_accesstoken==null) {
			System.out.println("�׼��� ��ū�� �����ϴ�.");
			response.sendError(401, "�׼��� ��ū�� �����ϴ�.");
			return false;
		}else {
			//�α׾ƿ��� ����������ū�̸�
			if(redisUtil.getData(user_refreshtoken)!=null) {
				System.out.println("�ش� �������� ��ū�� �̹� �α׾ƿ� ó���Ǿ����ϴ�.");
				response.sendError(401, "�ش� �������� ��ū�� �̹� �α׾ƿ� ó���Ǿ����ϴ�.");
				return false;
			}
			
			//�α׾ƿ��� �׼�����ū�̸�
			if(redisUtil.getData(user_accesstoken)!=null) {
				System.out.println("�ش� �׼��� ��ū�� �̹� �α׾ƿ� ó���Ǿ����ϴ�.");
				response.sendError(401, "�ش� �׼��� ��ū�� �̹� �α׾ƿ� ó���Ǿ����ϴ�.");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_accesstoken);
			}catch(ExpiredJwtException e) {
				System.out.println("�׼��� ��ū�� ������ ����Ǿ����ϴ�.");
			}catch(Exception e) {
				System.out.println("�׼��� ��ū�� �����Ǿ��ų� �߸��Ǿ����ϴ�.");
				response.sendError(401, "�׼��� ��ū�� �����Ǿ��ų� �߸��Ǿ����ϴ�.");
				return false;
			}
			
			try {
				jwtUtil.validateToken(user_refreshtoken);
				
				//�׼����� �������� id�� ������ Ȯ��
				String access_user_id = jwtUtil.getData(user_accesstoken, "user_id");
				String refresh_user_id = jwtUtil.getData(user_refreshtoken, "user_id");
				
				//���� ������̾��� �׼����� ������������ Ȯ��
				if(!access_user_id.equals(refresh_user_id)) {
					System.out.println("�׼��� ��ū�� �������� ��ū�� ����� ������ ��ġ���� �ʽ��ϴ�.");
					response.sendError(401, "�׼��� ��ū�� �������� ��ū�� ����� ������ ��ġ���� �ʽ��ϴ�.");
					return false;
				}
				
				System.out.println("�׼��� �������� ��ū�� ��� �����Դϴ�.");
				return true;
			}catch(ExpiredJwtException e) {
				System.out.println("�������� ��ū�� ������ ����Ǿ����ϴ�.");
				response.sendError(401, "�������� ��ū�� ������ ����Ǿ����ϴ�.");
				return false;
			}catch(Exception e) {
				System.out.println("�������� ��ū�� �����Ǿ��ų� �߸��Ǿ����ϴ�.");
				response.sendError(401, "�������� ��ū�� �����Ǿ��ų� �߸��Ǿ����ϴ�.");
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
