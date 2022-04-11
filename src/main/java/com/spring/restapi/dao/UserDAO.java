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
import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.exception.user.UnableToUpdateCountsException;

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
	
	public void join(HashMap param) throws Exception{
		if(sqlSession.insert("user.join", param)==0) {
			throw new Exception("ȸ�������� �õ��ϴ� �������� ������ �߻��߽��ϴ�.");
		}
	}
	
	public List getQuestions() throws Exception{
		return sqlSession.selectList("user.getQuestions");
	}
}