package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.user.DuplicateEmailException;
import com.spring.restapi.exception.user.DuplicateIdException;

@Repository("userDAO")
public class UserDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	//ȸ�����Խ� �Է��� ID�� �̹� ������� ID���� �Ǵ���.
	public String checkDuplicateId(HashMap param) throws DuplicateIdException{
		String user_id = sqlSession.selectOne("user.checkDuplicateId", param);
		if(user_id!=null) {
			throw new DuplicateIdException();
		}
		return user_id;
	}
	
	//ȸ�����Խ� �Է��� �̸����� �̹� ������� �̸������� �Ǵ���.
	public String checkDuplicateEmail(HashMap param) throws DuplicateEmailException{
		String user_email = sqlSession.selectOne("user.checkDuplicateEmail", param);
		if(user_email!=null) {
			throw new DuplicateEmailException();
		}
		return user_email;
	}
	
	//�Է¹��� ȸ������������ �̿��� ���ο� ȸ�������� DB�� �����.
	public void join(HashMap param) throws Exception{
		if(sqlSession.insert("user.join", param)==0) {
			throw new Exception("ȸ�������� �õ��ϴ� �������� ������ �߻��߽��ϴ�.");
		}
	}
	
	//��й�ȣ ã�� ���� ����� �߱���.
	public List getQuestions() throws Exception{
		return sqlSession.selectList("user.getQuestions");
	}
	
	//�ش� ������ ���� ���� ������� ��ȯ��.
	public List getRightChoices(HashMap param) {
		return sqlSession.selectList("problem.getRightChoices", param);
	}
	
	//Ŭ���̾�Ʈ�κ��� ���� ��ȣ �� �����̶�� ������ �����ȣ�� �Է¹޾� �����.
	public void insertRecords(HashMap param){
		sqlSession.insert("user.insertRecords", param);
	}

	public HashMap readUserInfo(HashMap param) {
		return sqlSession.selectOne("user.readUserInfo", param); 
	}
}