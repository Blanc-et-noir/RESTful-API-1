package com.spring.restapi.controller;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.service.UserService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

@Controller("userController")
public class UserController {
	@Autowired
	private UserService userService;
	
	//로그인 및 회원가입시에 중요한 정보를 암호화할때 사용할 공개키를 발급하고, 이에 대응되는 비밀키를 Redis에 저장함.
	@RequestMapping(value= {"/users/publickeys"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getPublickey(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 해당 사용자가 사용할 수 있는 공개키와 비밀키를 발급하고, 공개키는 클라이언트에게 발급하고, 비밀키는 Redis에 저장함.
		try {
			//2. RSA2048 비밀키, 공개키 키 한쌍을 발급함.
			KeyPair keyPair = RSA2048.createKey();
			String privatekey = RSA2048.keyToString(keyPair.getPrivate());
			String publickey = RSA2048.keyToString(keyPair.getPublic());
			
			//3. 비밀키는 Redis에 짧은시간동안 저장함.
			RedisUtil.setData(publickey, privatekey,JwtUtil.privateKeyMaxAge);
			
			//4. 공개키는 클라이언트로 전달함.
			result.put("flag", true);
			result.put("content", "공개키 발급 성공");
			result.put("publickey", publickey);
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "공개키 발급 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//비밀번호 분실시에 비밀번호를 찾을 수 있도록 비밀번호 찾기 질문 목록을 발급함.
	@RequestMapping(value= {"/users/questions"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> getQuestions(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 회원가입시에 선택할 수 있는 비밀번호 찾기 질문들을 발급함.
		try {	
			result.put("flag", true);
			result.put("content", "질문 목록 발급 성공");
			result.put("list", userService.getQuestions());
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "질문 목록 발급 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//새로 회원정보를 등록함. 비밀번호와 비밀번호 찾기 질문에 대한 답은 RSA2048암호화되어 컨트롤러로 전송되었음.
	@RequestMapping(value= {"/users"}, method={RequestMethod.POST})
	public ResponseEntity<HashMap> join(@RequestParam HashMap<String,String> param, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. 클라이언트로부터 전달받은 값들을 이용해 DB에 새로운 회원 정보를 등록함.
		try {
			userService.join(param);
			result.put("flag", true);
			result.put("content", "회원가입 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//2. 전달받은 ID를 이미 다른 회원이 사용함.
		}catch(DuplicateIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//3. 전달받은 이메일을 이미 다른 회원이 사용함.
		}catch(DuplicateEmailException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//4. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "회원가입 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//해당 액세스 토큰의 소유자 정보를 조회함.
	@RequestMapping(value= {"/users"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> info(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 기타 예외가 발생한 경우.
		try {
			//2. 해당 클라이언트의 액세스 토큰을 얻음.
			String user_accesstoken = CookieUtil.getAccesstoken(request);
			
			//3. 해당 액세스 토큰으로부터 사용자 정보및 토큰 남은 유효시간을 추출함.
			HashMap user = new HashMap();
			user.put("user_id", JwtUtil.getData(user_accesstoken, "user_id"));
			user.put("user_accesstoken_exp", (JwtUtil.getExpiration(user_accesstoken)/1000)+"초");
			
			HashMap param = new HashMap();
			param.put("user_id", JwtUtil.getData(user_accesstoken, "user_id"));
			user = userService.readUserInfo(param);
			
			//4. 해당 액세스 토큰의 정보를 클라이언트에게 전달함.
			result.put("user", user);
			result.put("flag", true);
			result.put("content", "회원정보 조회 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "조회 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value= {"/users/records"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> scoreProblems(@RequestBody List<HashMap> list, HttpServletRequest request){
		HashMap result = new HashMap();
		HashMap param = new HashMap();
		//1. 각 문제의 ID와 선택한 보기 ID를 배열로 전달받고 이를 통해 채점 및 그에 대한 결과를 반환함.
		try {
			//2. 해당 사용자의 ID를 얻음.
			String user_id = JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id");
			
			//3. 문제 채점후 정답수, 오답수, 정답률 등의 정보를 반환함.
			param.put("user_id", user_id);
			param.put("list", list);
			result = userService.scoreProblems(param);
			result.put("flag", true);
			result.put("content", "채점 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//4. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "채점 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}