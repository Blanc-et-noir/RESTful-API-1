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
	
	//�α��� �� ȸ�����Խÿ� �߿��� ������ ��ȣȭ�Ҷ� ����� ����Ű�� �߱��ϰ�, �̿� �����Ǵ� ���Ű�� Redis�� ������.
	@RequestMapping(value= {"/users/publickeys"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getPublickey(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. �ش� ����ڰ� ����� �� �ִ� ����Ű�� ���Ű�� �߱��ϰ�, ����Ű�� Ŭ���̾�Ʈ���� �߱��ϰ�, ���Ű�� Redis�� ������.
		try {
			//2. RSA2048 ���Ű, ����Ű Ű �ѽ��� �߱���.
			KeyPair keyPair = RSA2048.createKey();
			String privatekey = RSA2048.keyToString(keyPair.getPrivate());
			String publickey = RSA2048.keyToString(keyPair.getPublic());
			
			//3. ���Ű�� Redis�� ª���ð����� ������.
			RedisUtil.setData(publickey, privatekey,JwtUtil.privateKeyMaxAge);
			
			//4. ����Ű�� Ŭ���̾�Ʈ�� ������.
			result.put("flag", true);
			result.put("content", "����Ű �߱� ����");
			result.put("publickey", publickey);
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "����Ű �߱� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//��й�ȣ �нǽÿ� ��й�ȣ�� ã�� �� �ֵ��� ��й�ȣ ã�� ���� ����� �߱���.
	@RequestMapping(value= {"/users/questions"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> getQuestions(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ȸ�����Խÿ� ������ �� �ִ� ��й�ȣ ã�� �������� �߱���.
		try {	
			result.put("flag", true);
			result.put("content", "���� ��� �߱� ����");
			result.put("list", userService.getQuestions());
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ��� �߱� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//���� ȸ�������� �����. ��й�ȣ�� ��й�ȣ ã�� ������ ���� ���� RSA2048��ȣȭ�Ǿ� ��Ʈ�ѷ��� ���۵Ǿ���.
	@RequestMapping(value= {"/users"}, method={RequestMethod.POST})
	public ResponseEntity<HashMap> join(@RequestParam HashMap<String,String> param, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. Ŭ���̾�Ʈ�κ��� ���޹��� ������ �̿��� DB�� ���ο� ȸ�� ������ �����.
		try {
			userService.join(param);
			result.put("flag", true);
			result.put("content", "ȸ������ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//2. ���޹��� ID�� �̹� �ٸ� ȸ���� �����.
		}catch(DuplicateIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//3. ���޹��� �̸����� �̹� �ٸ� ȸ���� �����.
		}catch(DuplicateEmailException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//4. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "ȸ������ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�ش� �׼��� ��ū�� ������ ������ ��ȸ��.
	@RequestMapping(value= {"/users"}, method={RequestMethod.GET})
	public ResponseEntity<HashMap> info(HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ��Ÿ ���ܰ� �߻��� ���.
		try {
			//2. �ش� Ŭ���̾�Ʈ�� �׼��� ��ū�� ����.
			String user_accesstoken = CookieUtil.getAccesstoken(request);
			
			//3. �ش� �׼��� ��ū���κ��� ����� ������ ��ū ���� ��ȿ�ð��� ������.
			HashMap user = new HashMap();
			user.put("user_id", JwtUtil.getData(user_accesstoken, "user_id"));
			user.put("user_accesstoken_exp", (JwtUtil.getExpiration(user_accesstoken)/1000)+"��");
			
			HashMap param = new HashMap();
			param.put("user_id", JwtUtil.getData(user_accesstoken, "user_id"));
			user = userService.readUserInfo(param);
			
			//4. �ش� �׼��� ��ū�� ������ Ŭ���̾�Ʈ���� ������.
			result.put("user", user);
			result.put("flag", true);
			result.put("content", "ȸ������ ��ȸ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��ȸ ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value= {"/users/records"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> scoreProblems(@RequestBody List<HashMap> list, HttpServletRequest request){
		HashMap result = new HashMap();
		HashMap param = new HashMap();
		//1. �� ������ ID�� ������ ���� ID�� �迭�� ���޹ް� �̸� ���� ä�� �� �׿� ���� ����� ��ȯ��.
		try {
			//2. �ش� ������� ID�� ����.
			String user_id = JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id");
			
			//3. ���� ä���� �����, �����, ����� ���� ������ ��ȯ��.
			param.put("user_id", user_id);
			param.put("list", list);
			result = userService.scoreProblems(param);
			result.put("flag", true);
			result.put("content", "ä�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//4. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "ä�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}