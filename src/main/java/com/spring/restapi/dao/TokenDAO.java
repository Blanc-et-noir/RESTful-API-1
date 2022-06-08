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

	//해당 사용자가 현재 사용중인 액세스, 리프레쉬 토큰들을 새로운 액세스, 리프레쉬 토큰으로 갱신함.
	public void updateTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("token.updateTokens", param)==0) {
			throw new Exception("토큰을 DB에 업데이트하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	//해당 사용자가 현재 사용중인 액세스, 리프레쉬 토큰들을 DB에서 삭제함.
	public void deleteTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("token.deleteTokens", param)==0) {
			throw new Exception("토큰을 DB에서 삭제하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	//해당 사용자가 현재 사용중인 액세스, 리프레쉬 토큰 정보를 DB에서 얻음.
	public HashMap getTokens(HashMap param){
		return sqlSession.selectOne("token.getTokens", param);
	}
	
	//해당 사용자가 발급받았던 솔트값을 얻음.
	public String getSalt(HashMap<String, String> param){
		String user_salt = sqlSession.selectOne("token.getSalt", param);
		return user_salt;
	}
	
	//해당 ID로 가입한 사용자 정보에 대한 PW가 일치하는지 판단함, 일치하지 않으면 InvalidPwException 예외 발생.
	public String checkPw(HashMap param) throws InvalidPwException{
		String user_pw = sqlSession.selectOne("token.checkPw", param);
		if(user_pw==null||user_pw.equals("")) {
			throw new InvalidPwException();
		}
		return user_pw;
	}
	
	//해당 ID로 가입한 사용자 정보가 존재하는지 판단함, 존재하지 않으면 InvalidIdException 예외 발생.
	public String checkId(HashMap param) throws InvalidIdException{
		String user_id = sqlSession.selectOne("token.checkId", param);
		if(user_id==null||user_id.equals("")) {
			throw new InvalidIdException();
		}
		return user_id;
	}
}