package com.spring.restapi.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.ExpiredJwtException;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		InvalidIdException.class,
		InvalidPwException.class,
		DuplicateIdException.class,
		DuplicateEmailException.class
		}
)
@Service("UserService")
public class UserService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CookieUtil cookieUtil;
	

	
	public void login(HashMap<String,String> param, HttpServletResponse response) throws InvalidIdException, InvalidPwException, Exception{

		//해당 ID가 있는지 확인
		String user_id = userDAO.checkId(param);
		
		//솔트값 얻음
		String user_salt = userDAO.getSalt(param);
		
		//비밀키를 얻음
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//비밀키로 해싱
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//비밀번호 유효성 검사
		user_pw = userDAO.checkPw(param);
		
		//액세스, 리프레쉬 토큰 발급
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		
		String user_accesstoken = jwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = jwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//액세스, 리프레쉬 토큰 DB에 저장
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		userDAO.updateTokens(param);
		
		//액세스, 리프레쉬 토큰 쿠키에 첨부
		response.addCookie(cookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi"));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/token"));
	}
	
	public void logout(HashMap<String,String> param,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		try {
			redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		}catch(ExpiredJwtException e) {
			System.out.println("액세스토큰 유효기간이 지나 블랙리스트에 추가할 필요 X");
		}
		
		try {
			redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		}catch(ExpiredJwtException e) {
			System.out.println("리프레시토큰 유효기간이 지나 블랙리스트에 추가할 필요 X");
		}
		
		//액세스, 리프레쉬 토큰 쿠키에 첨부
		response.addCookie(cookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken","removed","/restapi/token",0));
	}
	
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
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//회원가입시도
		userDAO.join(param);
	}
	
	
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}
	public void validateTokens(HashMap param) throws Exception{
		userDAO.validateTokens(param);
	}
}
