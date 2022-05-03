package com.spring.restapi.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.UserDAO;
import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.encrypt.SHA;
import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		DuplicateIdException.class,
		DuplicateEmailException.class,
		InvalidIdException.class,
		InvalidPwException.class
		}
)
@Service("userService")
public class UserService {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CookieUtil cookieUtil;
	
	public void join(HashMap<String,String> param) throws DuplicateIdException, DuplicateEmailException, Exception{

		//중복된 ID가 있는지 확인
		String user_id = userDAO.checkDuplicateId(param);
		
		//중복된 이메일이 있는지 확인
		String user_email = userDAO.checkDuplicateEmail(param);
		
		//솔트 생성
		String user_salt = SHA.getSalt();
		param.put("user_salt", user_salt);
		
		//비밀키를 얻음
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//비밀키로 해싱
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey).replaceAll(" ", ""),user_salt);
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey).replaceAll(" ", ""),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//회원가입시도
		userDAO.join(param);
	}

	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}
}