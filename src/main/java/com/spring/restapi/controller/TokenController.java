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

//토큰 발급, 갱신, 삭제 요청을 담당하는 컨트롤러
@Controller("tokenController")
public class TokenController {
	@Autowired
	private TokenService tokenService;
	
	//사용자의 ID, PW 정보가 유효하다면 액세스, 리프레쉬 토큰을 새로 발급함.
	@RequestMapping(value={"/tokens"}, method={RequestMethod.POST})
	public ResponseEntity<HashMap> createTokens(@RequestParam HashMap param, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. 사용자로부터 ID, PW, 공개키를 전달받아 이를 서비스로 전달함.
		try {
			tokenService.createTokens(param,response);
			result.put("flag", true);
			result.put("content", "로그인에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//2. 해당 ID로 가입한 사용자 정보가 존재하지 않음.
		}catch(InvalidIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//3. 해당 ID로 가입한 사용자 정보에 대한 PW가 일치하지 않음.
		}catch(InvalidPwException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//4. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "로그인에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//해당 사용자의 액세스, 리프레쉬 토큰을 정상적으로 삭제 처리함.
	@RequestMapping(value={"/tokens"}, method={RequestMethod.DELETE})
	public ResponseEntity<HashMap> deleteTokens(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. 해당 사용자로부터 유효한 액세스, 리프레쉬 토큰을 전달받아 이를 서비스로 전달함.
		try {
			tokenService.deleteTokens(request, response);
			result.put("flag", true);
			result.put("content", "로그아웃에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "로그아웃에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//사용자의 액세스, 리프레쉬 토큰 갱신 요청을 처리함.
	@RequestMapping(value={"/tokens"}, method={RequestMethod.PUT})
	public ResponseEntity<HashMap> refreshTokens(HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. 사용자로부터 액세스, 리프레쉬 토큰을 전달받아 새로 토큰들을 발급하고 DB에 저장된 토큰 정보를 갱신함.
		try {
			tokenService.refreshTokens(request, response);
			result.put("flag", true);
			result.put("content", "액세스, 리프레시 토큰 갱신에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "액세스, 리프레시 토큰 갱신에 실패했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}