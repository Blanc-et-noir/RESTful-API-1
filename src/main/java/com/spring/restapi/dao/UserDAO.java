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
	
	//회원가입시 입력한 ID가 이미 사용중인 ID인지 판단함.
	public String checkDuplicateId(HashMap param) throws DuplicateIdException{
		String user_id = sqlSession.selectOne("user.checkDuplicateId", param);
		if(user_id!=null) {
			throw new DuplicateIdException();
		}
		return user_id;
	}
	
	//회원가입시 입력한 이메일이 이미 사용중인 이메일인지 판단함.
	public String checkDuplicateEmail(HashMap param) throws DuplicateEmailException{
		String user_email = sqlSession.selectOne("user.checkDuplicateEmail", param);
		if(user_email!=null) {
			throw new DuplicateEmailException();
		}
		return user_email;
	}
	
	//입력받은 회원가입정보를 이용해 새로운 회원정보를 DB에 등록함.
	public void join(HashMap param) throws Exception{
		if(sqlSession.insert("user.join", param)==0) {
			throw new Exception("회원가입을 시도하는 과정에서 오류가 발생했습니다.");
		}
	}
	
	//비밀번호 찾기 질문 목록을 발급함.
	public List getQuestions() throws Exception{
		return sqlSession.selectList("user.getQuestions");
	}
	
	//해당 문제에 대한 정답 보기들을 반환함.
	public List getRightChoices(HashMap param) {
		return sqlSession.selectList("problem.getRightChoices", param);
	}
	
	//클라이언트로부터 문제 번호 및 정답이라고 생각한 보기번호를 입력받아 기록함.
	public void insertRecords(HashMap param){
		sqlSession.insert("user.insertRecords", param);
	}

	public HashMap readUserInfo(HashMap param) {
		return sqlSession.selectOne("user.readUserInfo", param); 
	}
}