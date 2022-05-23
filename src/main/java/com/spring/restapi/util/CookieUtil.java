package com.spring.restapi.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
	
	public static Cookie createCookie(String name, String value, String path, int expiry) {
		Cookie cookie = new Cookie(name,value);
		cookie.setPath(path);
		cookie.setMaxAge(expiry);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		return cookie;
	}
	
	public static String getAccesstoken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String user_accesstoken = null;
		try {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("user_accesstoken")) {
					user_accesstoken = cookie.getValue();
					break;
				}
			}
			return user_accesstoken;
		}catch(Exception e) {
			return null;
		}
	}
	
	public static String getRefreshtoken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String user_refreshtoken = null;
		try {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("user_refreshtoken")) {
					user_refreshtoken = cookie.getValue();
					break;
				}
			}
			return user_refreshtoken;
		}catch(Exception e) {
			return null;
		}
	}
}
