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

		//�ش� ID�� �ִ��� Ȯ��
		String user_id = userDAO.checkId(param);
		
		//��Ʈ�� ����
		String user_salt = userDAO.getSalt(param);
		
		//���Ű�� ����
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//���Ű�� �ؽ�
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//��й�ȣ ��ȿ�� �˻�
		user_pw = userDAO.checkPw(param);
		
		//�׼���, �������� ��ū �߱�
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		
		String user_accesstoken = jwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = jwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//�׼���, �������� ��ū DB�� ����
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		userDAO.updateTokens(param);
		
		//�׼���, �������� ��ū ��Ű�� ÷��
		response.addCookie(cookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi"));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/token"));
	}
	
	public void logout(HashMap<String,String> param,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String user_accesstoken = cookieUtil.getAccesstoken(request);
		String user_refreshtoken = cookieUtil.getRefreshtoken(request);
		
		try {
			redisUtil.setData(user_accesstoken, "removed", jwtUtil.getExpiration(user_accesstoken));
		}catch(ExpiredJwtException e) {
			System.out.println("�׼�����ū ��ȿ�Ⱓ�� ���� ������Ʈ�� �߰��� �ʿ� X");
		}
		
		try {
			redisUtil.setData(user_refreshtoken, "removed", jwtUtil.getExpiration(user_refreshtoken));
		}catch(ExpiredJwtException e) {
			System.out.println("����������ū ��ȿ�Ⱓ�� ���� ������Ʈ�� �߰��� �ʿ� X");
		}
		
		//�׼���, �������� ��ū ��Ű�� ÷��
		response.addCookie(cookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(cookieUtil.createCookie("user_refreshtoken","removed","/restapi/token",0));
	}
	
	public void join(HashMap<String,String> param) throws DuplicateIdException, DuplicateEmailException, Exception{

		//�ߺ��� ID�� �ִ��� Ȯ��
		String user_id = userDAO.checkDuplicateId(param);
		
		//�ߺ��� �̸����� �ִ��� Ȯ��
		String user_email = userDAO.checkDuplicateEmail(param);
		
		//��Ʈ ����
		String user_salt = SHA.getSalt();
		param.put("user_salt", user_salt);
		
		//���Ű�� ����
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//���Ű�� �ؽ�
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//ȸ�����Խõ�
		userDAO.join(param);
	}
	
	
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}
	public void validateTokens(HashMap param) throws Exception{
		userDAO.validateTokens(param);
	}
}
