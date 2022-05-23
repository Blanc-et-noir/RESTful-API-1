package com.spring.restapi.interceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;

public class LogInterceptor implements HandlerInterceptor{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

		String user_id = JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id");
		
		logger.info("","");
		logger.info("URL : {} {}",request.getMethod(),request.getRequestURL());
		
		if(user_id!=null) {
			logger.info("USER_ID : {}",user_id);
		}
		
		logger.info("TIME : {}",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        logger.info("IP_ADDRESS : {}",request.getRemoteAddr());
        logger.info("ACCESS_TOKEN : {}",CookieUtil.getAccesstoken(request));
        logger.info("REFRESH_TOKEN : {}",CookieUtil.getRefreshtoken(request));

        Enumeration<String> enumeration = request.getParameterNames();
        if(enumeration.hasMoreElements()) {
        	logger.info("PARAMETERS","");
        }
        
        while(enumeration.hasMoreElements()) {
        	String name = enumeration.nextElement();
        	String value = request.getParameter(name);
        	logger.info("{} : {}",name,value);
        }
        
        logger.info("","");

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub
	}
}