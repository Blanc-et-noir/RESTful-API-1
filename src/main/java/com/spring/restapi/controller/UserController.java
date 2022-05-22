package com.spring.restapi.controller;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.service.UserService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

@Controller("userController")
public class UserController {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private CookieUtil cookieUtil;
	
	@RequestMapping(value= {"/users/publickeys"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getPublickey(HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			KeyPair keyPair = RSA2048.createKey();
			String privatekey = RSA2048.keyToString(keyPair.getPrivate());
			String publickey = RSA2048.keyToString(keyPair.getPublic());
			
			redisUtil.setData(publickey, privatekey,JwtUtil.privateKeyMaxAge);
			
			result.put("flag", true);
			result.put("content", "����Ű �߱� ����");
			result.put("publickey", publickey);
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "����Ű �߱� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value= {"/users/questions"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> getQuestions(HttpServletRequest request){
		HashMap result = new HashMap();
		try {	
			result.put("flag", true);
			result.put("content", "���� ��� �߱� ����");
			result.put("list", userService.getQuestions());
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ��� �߱� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//��ū �ʿ� ����
	@RequestMapping(value= {"/users"}, method={RequestMethod.POST})
	public ResponseEntity<HashMap> join(@RequestParam HashMap<String,String> param, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			userService.join(param);
			result.put("flag", true);
			result.put("content", "ȸ������ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		}catch(DuplicateIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(DuplicateEmailException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "ȸ������ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/users"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> info(HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			String user_accesstoken = cookieUtil.getAccesstoken(request);
			result.put("flag", "true");
			result.put("content", "�α��� ������ �ʿ��� ��� ��� ����\n��û�� ID : "+jwtUtil.getData(user_accesstoken, "user_id")+"\n�׼��� ��ū�� �ܿ� �ð� : "+(jwtUtil.getExpiration(user_accesstoken)/1000)+"��");
			
			HashMap map = new HashMap();
			map.put("user_id", jwtUtil.getData(user_accesstoken, "user_id"));
			map.put("user_accesstoken_exp", (jwtUtil.getExpiration(user_accesstoken)/1000)+"��");
			result.put("user_info", map);
			result.put("flag", true);
			result.put("content", "��ȸ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��ȸ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/users/records"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> scoreProblems(@RequestBody HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			String user_id = jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id");
			
			ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) param.get("list");
			
			for(int i=0;i<list.size();i++) {
				list.get(i).put("user_id", user_id);
			}
			
			result = userService.scoreProblems(param);
			result.put("flag", true);
			result.put("content", "ä�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		}catch(UnableToInsertRecordsException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "ä�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}