package com.spring.restapi.service;

import java.util.ArrayList;
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
	
	public List getProblems(HashMap param) {
		//문제와 보기를 적절하게 리스트와 해시맵의 조합으로 구성하기 위한 변수
		ArrayList<HashMap> result = new ArrayList<HashMap>();
		
		//각 문제들의 어레이리스트 인덱스를 임시로 저장할 해시맵
		HashMap<String, Integer> temp = new HashMap<String,Integer>();
		
		//문제번호 및 내용들을 리스트로 얻음
		List<HashMap> problems = userDAO.getProblems(param);
		
		for(HashMap<String,String> map: problems) {
			//해당 문제를 어레이리스트에 저장할 때 사용되는 인덱스를 임시로 저장함
			temp.put(String.valueOf(map.get("problem_id")), result.size());
			
			HashMap hm = new HashMap();
			hm.put("problem_id", map.get("problem_id"));
			hm.put("problem_content", map.get("problem_content"));
			hm.put("problem_image_name", map.get("problem_image_name"));
			hm.put("choices", new ArrayList<HashMap>());
			
			result.add(hm);
		}
		
		//해당 문제에 대응되는 보기들을 얻기 위해 파라미터에 저장함
		param.put("problems", problems);
		
		//보기들을 얻음
		List<HashMap> choices = userDAO.getChoices(param);

		
		for(HashMap<String,String> map: choices) {
			
			//이전에 임시로 저장했던 인덱스 값을 구함
			int idx = temp.get(String.valueOf(map.get("problem_id")));
			
			//해당 보기가 정답이라면 
			if(map.get("choice_yn").equalsIgnoreCase("Y")) {
				result.get(idx).put("answer_id", map.get("choice_id"));
				result.get(idx).put("answer_content", map.get("choice_content"));
			}
			
			//보기들을 저장하는 어레이리스트에 해당 보기에 대한 정보를 갖는 HashMap 객체를 추가함
			ArrayList<HashMap> arr = (ArrayList<HashMap>) ((result.get(idx)).get("choices"));
			
			//중복되는 정보는 이제 필요 없으므로 삭제함
			map.remove("problem_id");
			
			arr.add(map);
		}
		return result;
	}
	
	public HashMap scoreProblems(HashMap param) throws Exception{
		HashMap<String,Object> result = new HashMap<String,Object>();
		
		ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) param.get("list");
		System.out.println(list.size());
		//본인이 선택한 보기들을 리스트로 얻음
		list = (ArrayList<HashMap<String, String>>) userDAO.getChoicesInfo(param);
		
		List right_problems = new ArrayList();
		List wrong_problems = new ArrayList();
		
		//채점함
		int right = 0, wrong = 0;
		System.out.println(list.size());
		for(int i=0;i<list.size();i++) {
			if(list.get(i).get("choice_yn").equalsIgnoreCase("Y")) {
				right++;
				right_problems.add(list.get(i).get("problem_id"));
			}else {
				wrong++;
				wrong_problems.add(list.get(i).get("problem_id"));
			}
		}
		
		
		//채점결과 기록, 나중에 구현
		
		//채점결과 반환
		System.out.println(right+" "+wrong);
		result.put("percentage", (right*1.0/(right+wrong)*1.0)*100);
		result.put("right_score", right);
		result.put("wrong_score", wrong);
		result.put("right_problems", right_problems);
		result.put("wrong_problems", wrong_problems);
		return result;
	}
	
	public List getCategories(HashMap param){
		return userDAO.getCategories(param);
	}
	
	public List getQuestions() throws Exception{
		return userDAO.getQuestions();
	}
	public void validateTokens(HashMap param) throws Exception{
		userDAO.validateTokens(param);
	}
}
