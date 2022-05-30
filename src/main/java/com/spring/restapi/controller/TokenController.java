package com.spring.restapi.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;
import com.spring.restapi.service.TokenService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

@Controller("tokenController")
public class TokenController {
	@Autowired
	private TokenService tokenService;
	
	//액세스, 리프레쉬 필요
	@RequestMapping(value={"/tokens"}, method={RequestMethod.PUT})
	public ResponseEntity<HashMap> refreshTokens(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			tokenService.refreshTokens(request, response);
			result.put("flag", true);
			result.put("content", "액세스, 리프레시 토큰 갱신에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "액세스, 리프레시 토큰 갱신에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//토큰 필요없음
	@RequestMapping(value={"/tokens"}, method={RequestMethod.POST})
	public ResponseEntity<HashMap> login(@RequestParam HashMap<String,String> param, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			tokenService.login(param,response);
			result.put("flag", true);
			result.put("content", "로그인 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		}catch(InvalidIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(InvalidPwException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "로그인 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//액세스, 리프레쉬 필요
	@RequestMapping(value={"/tokens"}, method={RequestMethod.DELETE})
	public ResponseEntity<HashMap> logout(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			tokenService.logout(request, response);
			result.put("flag", true);
			result.put("content", "로그아웃에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "로그아웃에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}
