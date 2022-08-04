package com.spring.restapi.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.TokenDAO;
import com.spring.restapi.dao.UserDAO;
import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.encrypt.SHA;
import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.exception.user.NotCorrectQuestionAnswerException;
import com.spring.restapi.util.RedisUtil;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		DuplicateIdException.class,
		DuplicateEmailException.class,
		NotCorrectQuestionAnswerException.class
		}
)
@Service("userService")
public class UserService {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private TokenDAO tokenDAO;
	
	//Ŭ���̾�Ʈ�κ��� ���� ������ �������� DB�� ���ο� ȸ�������� �����.
	public void join(HashMap<String,String> param) throws DuplicateIdException, DuplicateEmailException, Exception{

		//1. �ش� ID�� �̹� ������� ID���� �ƴ��� Ȯ����.
		String user_id = userDAO.checkDuplicateId(param);
		
		//2. �ش� �̸����� �̹� ������� �̸������� �ƴ��� Ȯ����.
		String user_email = userDAO.checkDuplicateEmail(param);
		
		//3. ���κ������̺� ���� ������ ���� ������ ��Ʈ���� �ϳ� �߱���.
		String user_salt = SHA.getSalt();
		param.put("user_salt", user_salt);
		
		//4. Ŭ���̾�Ʈ�� ��ȣȭ�� ����ߴ� ����Ű�� ���� ���Ű�� Redis���� ����.
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//5. ��ȣȭ�� PW�� �ش� ���Ű�� ��ȣȭ�ϰ� ������ ������ ��, ������ ��Ʈ���� �̿��� SHA512�� �� �� �ؽ���.
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey).replaceAll(" ", ""),user_salt);
		
		//6. ��й�ȣ ã�� ������ ���� �� ���� �ش� ���Ű�� ��ȣȭ�� ������ �����ϰ�, ������ ��Ʈ���� �̿��� SHA512�� �� �� �ؽ���.
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey).replaceAll(" ", ""),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//7. �ش� ������ �̿��� DB�� ���ο� ȸ�� ������ �߰���.
		userDAO.join(param);
	}

	//�ش� ������� ����Ǯ�� ����� ä���ϰ�, ����, ���� ���� ��� �� ����� �ۼ�Ƽ�� ������ ��ȯ��.
	public HashMap scoreProblems(HashMap param) throws Exception{
		HashMap<String,Object> result = new HashMap<String,Object>();
		HashMap temp = new HashMap();
		
		//1. Ŭ���̾�Ʈ�� ä���ϰ��� �ϴ� ������ ���� ���� �����ȣ���� ����.
		List<HashMap<String,String>> right_choices = (List<HashMap<String, String>>) userDAO.getRightChoices(param);
		
		//2. Ŭ���̾�Ʈ�� �ش� ������ ���� �����̶�� ������ ���� ��ȣ���� ����.
		List<HashMap<String,String>> user_choices = (List<HashMap<String, String>>) param.get("list");
		
		//3. �� ������ �����Ǵ� ���� �����ȣ���� �ؽøʿ� ������.
		for(HashMap<String,String> choice : right_choices) {
			temp.put(choice.get("choice_id"), "answer");
		}
		
		//4. ä����� ������ ����, ������ ������ȣ�� ������ ����Ʈ ����. 
		List right_problems = new LinkedList();
		List wrong_problems = new LinkedList();

		//5. �����̶�� ���丮��Ʈ��, �����̶�� ���丮��Ʈ�� ���� ��ȣ�� �߰���.
		for(HashMap<String,String> choice : user_choices) {
			if(temp.containsKey(choice.get("choice_id"))) {
				right_problems.add(choice.get("problem_id"));
			}else {
				wrong_problems.add(choice.get("problem_id"));
			}
		}
		
		//6. ������ȣ�� �׿� �����Ǵ� �����̶�� ������ ���� ��ȣ�� �̿��� DB�� �����.
		userDAO.insertRecords(param);
		
		//7. �����, ���䰳��, ���䰳��, ���乮����ȣ, ���乮����ȣ�� ��ȯ��.
		result.put("percentage", (right_problems.size()*1.0/(right_problems.size()+wrong_problems.size())*1.0)*100);
		result.put("right_score", right_problems.size());
		result.put("wrong_score", +wrong_problems.size());
		result.put("right_problems", right_problems);
		result.put("wrong_problems", wrong_problems);
		return result;
	}
	
	//ȸ�����Խÿ� ������ ��й�ȣ ã�� ���� ����� �߱���.
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}

	public HashMap readUserInfo(HashMap param) {
		HashMap user = userDAO.readUserInfo(param);
		
		return user;
	}

	public void updateInfo(HashMap param) throws NotCorrectQuestionAnswerException, Exception {
		HashMap user  = userDAO.readUserInfo(param);
		
		String user_salt = tokenDAO.getSalt(param);
		
		//3. �ش� ����ڰ� ������ ����Ű�� ���� ���Ű�� ����.
		String privatekey = (String) RedisUtil.getData((String) param.get("publickey"));
		
		System.out.println(user_salt);
		System.out.println((String) param.get("publickey"));
		System.out.println(privatekey);
		System.out.println((String)param.get("old_question_answer"));
		
		String old_question_answer = SHA.DSHA512(RSA2048.decrypt((String)(param.get("old_question_answer")),privatekey).replaceAll(" ", ""),user_salt);
		if(!old_question_answer.equals((String)user.get("question_answer"))){
			throw new NotCorrectQuestionAnswerException();
		}
		
		String new_user_salt = SHA.getSalt();
		
		if(param.get("user_pw")!=null&&param.get("question_id")!=null&&param.get("question_answer")!=null) {
			System.out.println("���:"+(String)param.get("user_pw"));
			System.out.println("���Ű:"+privatekey);
			System.out.println("��Ʈ:"+new_user_salt);
			String user_pw = SHA.DSHA512(RSA2048.decrypt((String)param.get("user_pw"),privatekey).replaceAll(" ", ""),new_user_salt);
			
			String question_answer = SHA.DSHA512(RSA2048.decrypt((String)param.get("question_answer"),privatekey).replaceAll(" ", ""),new_user_salt);
			param.put("user_pw", user_pw);
			param.put("question_answer", question_answer);
			param.put("user_salt", new_user_salt);
		}
		
		userDAO.updateInfo(param);
	}
}