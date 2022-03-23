package com.spring.restapi.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.restapi.service.TokenService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

@Controller("TokenController")
public class TokenController {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private CookieUtil cookieUtil;
	
	@RequestMapping(value= {"/token/refreshTokens.do"})
	public ResponseEntity<HashMap> refreshTokens(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			tokenService.refreshTokens(request, response);
			result.put("flag", "false");
			result.put("content", "액세스, 리프레시 토큰 갱신에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", "false");
			result.put("content", "액세스, 리프레시 토큰 갱신에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value= {"/token/logout.do"})
	public ResponseEntity<HashMap> logout(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			tokenService.logout(request, response);
			result.put("flag", "false");
			result.put("content", "액세스, 리프레시 토큰 갱신에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", "false");
			result.put("content", "액세스, 리프레시 토큰 갱신에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}
	}
}
