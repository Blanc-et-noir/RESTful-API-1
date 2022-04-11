package com.spring.restapi.dao;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;

@Repository("tokenDAO")
public class TokenDAO {
	@Autowired
	private SqlSession sqlSession;

	public void updateTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("token.updateTokens", param)==0) {
			throw new Exception("토큰을 DB에 업데이트 하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	public HashMap getTokens(HashMap param){
		return sqlSession.selectOne("token.getTokens", param);
	}
	
	public String getSalt(HashMap<String, String> param){
		String user_salt = sqlSession.selectOne("token.getSalt", param);
		return user_salt;
	}
	
	public String checkPw(HashMap param) throws InvalidPwException{
		String user_pw = sqlSession.selectOne("token.checkPw", param);
		if(user_pw==null||user_pw.equals("")) {
			throw new InvalidPwException();
		}
		return user_pw;
	}
	
	public String checkId(HashMap param) throws InvalidIdException{
		String user_id = sqlSession.selectOne("token.checkId", param);
		if(user_id==null||user_id.equals("")) {
			throw new InvalidIdException();
		}
		return user_id;
	}
}