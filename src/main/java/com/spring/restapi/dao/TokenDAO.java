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

	//�ش� ����ڰ� ���� ������� �׼���, �������� ��ū���� ���ο� �׼���, �������� ��ū���� ������.
	public void updateTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("token.updateTokens", param)==0) {
			throw new Exception("��ū�� DB�� ������Ʈ�ϴ� �������� ������ �߻��߽��ϴ�.");
		}
	}
	
	//�ش� ����ڰ� ���� ������� �׼���, �������� ��ū���� DB���� ������.
	public void deleteTokens(HashMap<String, String> param) throws Exception{
		if(sqlSession.update("token.deleteTokens", param)==0) {
			throw new Exception("��ū�� DB���� �����ϴ� �������� ������ �߻��߽��ϴ�.");
		}
	}
	
	//�ش� ����ڰ� ���� ������� �׼���, �������� ��ū ������ DB���� ����.
	public HashMap getTokens(HashMap param){
		return sqlSession.selectOne("token.getTokens", param);
	}
	
	//�ش� ����ڰ� �߱޹޾Ҵ� ��Ʈ���� ����.
	public String getSalt(HashMap<String, String> param){
		String user_salt = sqlSession.selectOne("token.getSalt", param);
		return user_salt;
	}
	
	//�ش� ID�� ������ ����� ������ ���� PW�� ��ġ�ϴ��� �Ǵ���, ��ġ���� ������ InvalidPwException ���� �߻�.
	public String checkPw(HashMap param) throws InvalidPwException{
		String user_pw = sqlSession.selectOne("token.checkPw", param);
		if(user_pw==null||user_pw.equals("")) {
			throw new InvalidPwException();
		}
		return user_pw;
	}
	
	//�ش� ID�� ������ ����� ������ �����ϴ��� �Ǵ���, �������� ������ InvalidIdException ���� �߻�.
	public String checkId(HashMap param) throws InvalidIdException{
		String user_id = sqlSession.selectOne("token.checkId", param);
		if(user_id==null||user_id.equals("")) {
			throw new InvalidIdException();
		}
		return user_id;
	}
}