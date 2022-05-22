package com.spring.restapi.service;

import java.util.ArrayList;
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
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey).replaceAll(" ", ""),user_salt);
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey).replaceAll(" ", ""),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//ȸ�����Խõ�
		userDAO.join(param);
	}

	public HashMap scoreProblems(HashMap param) throws Exception{
		HashMap<String,Object> result = new HashMap<String,Object>();
		
		ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) param.get("list");
		//������ ������ ������� ����Ʈ�� ����
		list = (ArrayList<HashMap<String, String>>) userDAO.getChoicesInfo(param);
		
		List right_problems = new ArrayList();
		List wrong_problems = new ArrayList();
		
		//ä����
		int right = 0, wrong = 0;
		for(int i=0;i<list.size();i++) {
			if(list.get(i).get("choice_yn").equalsIgnoreCase("Y")) {
				right++;
				right_problems.add(list.get(i).get("problem_id"));
			}else {
				wrong++;
				wrong_problems.add(list.get(i).get("problem_id"));
			}
		}
		
		userDAO.insertRecords(param);
		
		//ä����� ��ȯ
		result.put("percentage", (right*1.0/(right+wrong)*1.0)*100);
		result.put("right_score", right);
		result.put("wrong_score", wrong);
		result.put("right_problems", right_problems);
		result.put("wrong_problems", wrong_problems);
		return result;
	}
	
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}
}