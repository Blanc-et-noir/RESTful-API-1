package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.exception.user.UnableToUpdateCountsException;

@Repository("problemDAO")
public class ProblemDAO {
	@Autowired
	private SqlSession sqlSession;
	
	public void insertRecords(HashMap param) throws UnableToInsertRecordsException{
		if(sqlSession.insert("problem.insertRecords", param)==0) {
			throw new UnableToInsertRecordsException();
		}
	}
	
	public void updateChoiceCounts(HashMap param) throws UnableToUpdateCountsException{
		if(sqlSession.update("problem.updateChoiceCounts", param)==0) {
			throw new UnableToUpdateCountsException();
		}
	}
	
	public List getProblems(HashMap param) {
		return sqlSession.selectList("problem.getProblems", param);
	}
	
	
	public List getChoicesInfo(HashMap param) {
		return sqlSession.selectList("problem.getChoicesInfo", param);
	}
	
	public List getCategories(HashMap param){
		return sqlSession.selectList("problem.getCategories", param);
	}
}