package com.spring.restapi.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
	public Cookie createCookie(String name, String value, String path, int expiry) {
		Cookie cookie = new Cookie(name,value);
		cookie.setPath(path);
		cookie.setMaxAge(expiry);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		return cookie;
	}
	public Cookie createCookie(String name, String value, String path) {
		Cookie cookie = new Cookie(name,value);
		cookie.setPath(path);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		return cookie;
	}
	public String getAccesstoken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("user_accesstoken")) {
				return cookie.getValue();
			}
		}
		return null;
	}
	public String getRefreshtoken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("user_refreshtoken")) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
