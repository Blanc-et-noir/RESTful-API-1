package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.problem.InvalidOpinionIdException;
import com.spring.restapi.exception.problem.InvalidProblemIdException;
import com.spring.restapi.exception.problem.InvalidUserIdException;
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
	
	public void writeOpinion(HashMap param) throws Exception {
		if(sqlSession.insert("problem.writeOpinion", param)==0) {
			throw new Exception();
		}
	}
	
	public List readOpinions(HashMap param) throws Exception {
		return sqlSession.selectList("problem.readOpinions", param);
	}
	
	public int getOpinionsTotal(HashMap param) {
		return sqlSession.selectOne("problem.getOpinionsTotal", param);
	}
	
	public void checkProblemId(HashMap param) throws InvalidProblemIdException{
		if(sqlSession.selectOne("problem.checkProblemId",param)==null) {
			throw new InvalidProblemIdException();
		}
	}
	
	public void checkOpinionId(HashMap param) throws InvalidOpinionIdException{
		if(sqlSession.selectOne("problem.checkOpinionId",param)==null) {
			throw new InvalidOpinionIdException();
		}
	}
	
	public void checkUserId(HashMap param) throws InvalidUserIdException{
		if(sqlSession.selectOne("problem.checkUserId",param)==null) {
			throw new InvalidUserIdException();
		}
	}
	
	public void deleteOpinion(HashMap param) throws Exception{
		if(sqlSession.delete("problem.deleteOpinion",param)==0) {
			throw new Exception();
		}
	}
	
	public void updateOpinion(HashMap param) throws Exception{
		if(sqlSession.update("problem.updateOpinion",param)==0) {
			throw new Exception();
		}
	}
}