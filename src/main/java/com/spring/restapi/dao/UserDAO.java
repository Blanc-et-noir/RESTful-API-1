package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;
import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;

@Repository("userDAO")
public class UserDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	public String checkDuplicateId(HashMap param) throws DuplicateIdException{
		String user_id = sqlSession.selectOne("user.checkDuplicateId", param);
		if(user_id!=null) {
			throw new DuplicateIdException();
		}
		return user_id;
	}
	
	public String checkDuplicateEmail(HashMap param) throws DuplicateEmailException{
		String user_email = sqlSession.selectOne("user.checkDuplicateEmail", param);
		if(user_email!=null) {
			throw new DuplicateEmailException();
		}
		return user_email;
	}
	
	public String checkId(HashMap param) throws InvalidIdException{
		String user_id = sqlSession.selectOne("user.checkId", param);
		if(user_id==null||user_id.equals("")) {
			throw new InvalidIdException();
		}
		return user_id;
	}
	
	public String checkPw(HashMap param) throws InvalidPwException{
		String user_pw = sqlSession.selectOne("user.checkPw", param);
		if(user_pw==null||user_pw.equals("")) {
			throw new InvalidPwException();
		}
		return user_pw;
	}

	public String getSalt(HashMap<String, String> param){
		String user_salt = sqlSession.selectOne("user.getSalt", param);
		return user_salt;
	}
	
	public void updateTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("user.updateTokens", param)==0) {
			throw new Exception("토큰을 DB에 업데이트 하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	public HashMap getTokens(HashMap param){
		return sqlSession.selectOne("user.getTokens", param);
	}
	
	public void join(HashMap param) throws Exception{
		if(sqlSession.insert("user.join", param)==0) {
			throw new Exception("회원가입을 시도하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	public void insertRecords(HashMap param) throws Exception{
		if(sqlSession.insert("user.insertRecords", param)==0) {
			throw new Exception("채점기록 추가 실패");
		}
	}
	
	public List getQuestions() throws Exception{
		return sqlSession.selectList("user.getQuestions");
	}
	
	public List getChoices(HashMap param) {
		return sqlSession.selectList("user.getChoices", param);
	}
	
	public List getChoicesInfo(HashMap param) {
		return sqlSession.selectList("user.getChoicesInfo", param);
	}
	
	public List getProblems(HashMap param) {
		return sqlSession.selectList("user.getProblems", param);
	}
	
	public List getCategories(HashMap param){
		return sqlSession.selectList("user.getCategories", param);
	}
}