package com.spring.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.ProblemDAO;
import com.spring.restapi.exception.problem.InvalidOpinionIdException;
import com.spring.restapi.exception.problem.InvalidProblemIdException;
import com.spring.restapi.exception.problem.InvalidUserIdException;

@Service("problemService")
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		InvalidProblemIdException.class,
		InvalidOpinionIdException.class,
		InvalidUserIdException.class
		}
)
public class ProblemService {
	@Autowired
	ProblemDAO problemDAO;
	
	public List getProblems(HashMap param) {
		//문제와 보기를 적절하게 리스트와 해시맵의 조합으로 구성하기 위한 변수
		ArrayList<HashMap> result = new ArrayList<HashMap>();
		
		//각 문제들의 어레이리스트 인덱스를 임시로 저장할 해시맵
		HashMap<Object, Integer> temp = new HashMap<Object,Integer>();
		
		//문제 및 보기들을 리스트로 얻음
		List<HashMap> problems = problemDAO.getProblems(param);
		
		for(HashMap map : problems) {
			int idx;
			if(temp.get(map.get("problem_id"))!=null) {
				idx = temp.get(map.get("problem_id"));
			}else {
				idx = result.size();
				temp.put(map.get("problem_id"), idx);
				HashMap problem = new HashMap();
				problem.put("problem_id", map.get("problem_id"));
				problem.put("problem_content", map.get("problem_content"));
				problem.put("problem_image_name", map.get("problem_image_name"));
				problem.put("choices",new ArrayList<HashMap>());
				result.add(problem);
			}

			ArrayList<HashMap> choices = (ArrayList<HashMap>) result.get(idx).get("choices");
			HashMap choice = new HashMap();
			choice.put("choice_id", map.get("choice_id"));
			choice.put("choice_content", map.get("choice_content"));
			choice.put("choice_yn", map.get("choice_yn"));
			choice.put("choice_count", map.get("choice_count"));
			choice.put("pick_rate", map.get("pick_rate"));
			choices.add(choice);
			
			if(map.get("choice_yn").equals("Y")) {
				HashMap problem = result.get(idx);
				problem.put("answer_rate", map.get("pick_rate"));
				problem.put("answer_id", map.get("choice_id"));
			}
		}
		return result;
	}
	
	public void writeOpinion(HashMap param) throws InvalidProblemIdException, Exception{
		problemDAO.checkProblemId(param);
		problemDAO.writeOpinion(param);
	}
	
	public void deleteOpinion(HashMap param) throws InvalidProblemIdException,InvalidOpinionIdException, InvalidUserIdException, Exception{
		problemDAO.checkProblemId(param);
		problemDAO.checkOpinionId(param);
		problemDAO.checkUserId(param);
		problemDAO.deleteOpinion(param);
	}
	
	public void updateOpinion(HashMap param) throws InvalidProblemIdException,InvalidOpinionIdException, InvalidUserIdException, Exception{
		problemDAO.checkProblemId(param);
		problemDAO.checkOpinionId(param);
		problemDAO.checkUserId(param);
		problemDAO.updateOpinion(param);
	}
	
	public HashMap readOpinions(HashMap param) throws InvalidProblemIdException, Exception{
		HashMap result = new HashMap();
		problemDAO.checkProblemId(param);
		int total = problemDAO.getOpinionsTotal(param);
		List list = problemDAO.readOpinions(param);
		
		result.put("total", total);
		result.put("list", list);
		return result;
	}
	
	public List getCategories(HashMap param){
		return problemDAO.getCategories(param);
	}
}