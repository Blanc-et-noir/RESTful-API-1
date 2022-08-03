package com.spring.restapi.service;

import java.util.HashMap;
import java.util.LinkedList;
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
import com.spring.restapi.util.RedisUtil;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		DuplicateIdException.class,
		DuplicateEmailException.class
		}
)
@Service("userService")
public class UserService {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserDAO userDAO;
	
	//클라이언트로부터 받은 정보를 바탕으로 DB에 새로운 회원정보를 등록함.
	public void join(HashMap<String,String> param) throws DuplicateIdException, DuplicateEmailException, Exception{

		//1. 해당 ID가 이미 사용중인 ID인지 아닌지 확인함.
		String user_id = userDAO.checkDuplicateId(param);
		
		//2. 해당 이메일이 이미 사용중인 이메일인지 아닌지 확인함.
		String user_email = userDAO.checkDuplicateEmail(param);
		
		//3. 레인보우테이블 공격 방지를 위해 무작위 솔트값을 하나 발급함.
		String user_salt = SHA.getSalt();
		param.put("user_salt", user_salt);
		
		//4. 클라이언트가 암호화에 사용했던 공개키에 대한 비밀키를 Redis에서 얻음.
		String privatekey = (String) redisUtil.getData(param.get("publickey"));
		
		//5. 암호화된 PW를 해당 비밀키로 복호화하고 공백을 제거한 후, 무작위 솔트값을 이용해 SHA512로 두 번 해싱함.
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey).replaceAll(" ", ""),user_salt);
		
		//6. 비밀번호 찾기 질문에 대한 답 또한 해당 비밀키로 복호화후 공백을 제거하고, 무작위 솔트값을 이용해 SHA512로 두 번 해싱함.
		String question_answer = SHA.DSHA512(RSA2048.decrypt(param.get("question_answer"), privatekey).replaceAll(" ", ""),user_salt);
		param.put("user_pw", user_pw);
		param.put("question_answer", question_answer);
		
		//7. 해당 정보를 이용해 DB에 새로운 회원 정보를 추가함.
		userDAO.join(param);
	}

	//해당 사용자의 문제풀이 결과를 채점하고, 정답, 오답 문제 목록 및 정답률 퍼센티지 정보를 반환함.
	public HashMap scoreProblems(HashMap param) throws Exception{
		HashMap<String,Object> result = new HashMap<String,Object>();
		HashMap temp = new HashMap();
		
		//1. 클라이언트가 채점하고자 하는 문제에 대한 정답 보기번호들을 얻음.
		List<HashMap<String,String>> right_choices = (List<HashMap<String, String>>) userDAO.getRightChoices(param);
		
		//2. 클라이언트가 해당 문제에 대해 정답이라고 생각한 보기 번호들을 얻음.
		List<HashMap<String,String>> user_choices = (List<HashMap<String, String>>) param.get("list");
		
		//3. 각 문제에 대응되는 정답 보기번호들을 해시맵에 저장함.
		for(HashMap<String,String> choice : right_choices) {
			temp.put(choice.get("choice_id"), "answer");
		}
		
		//4. 채점결과 정답인 문제, 오답인 문제번호를 저장할 리스트 생성. 
		List right_problems = new LinkedList();
		List wrong_problems = new LinkedList();

		//5. 정답이라면 정답리스트에, 오답이라면 오답리스트에 문제 번호를 추가함.
		for(HashMap<String,String> choice : user_choices) {
			if(temp.containsKey(choice.get("choice_id"))) {
				right_problems.add(choice.get("problem_id"));
			}else {
				wrong_problems.add(choice.get("problem_id"));
			}
		}
		
		//6. 문제번호와 그에 대응되는 정답이라고 생각한 보기 번호를 이용해 DB에 기록함.
		userDAO.insertRecords(param);
		
		//7. 정답률, 정답개수, 오답개수, 정답문제번호, 오답문제번호를 반환함.
		result.put("percentage", (right_problems.size()*1.0/(right_problems.size()+wrong_problems.size())*1.0)*100);
		result.put("right_score", right_problems.size());
		result.put("wrong_score", +wrong_problems.size());
		result.put("right_problems", right_problems);
		result.put("wrong_problems", wrong_problems);
		return result;
	}
	
	//회원가입시에 선택할 비밀번호 찾기 질문 목록을 발급함.
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}

	public HashMap readUserInfo(HashMap param) {
		HashMap user = userDAO.readUserInfo(param);
		
		return user;
	}
}