package com.spring.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.ProblemDAO;
import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.exception.user.UnableToUpdateCountsException;

@Service("problemService")
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class,
		UnableToUpdateCountsException.class,
		UnableToInsertRecordsException.class,
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
	
	public HashMap scoreProblems(HashMap param) throws UnableToUpdateCountsException,Exception{
		HashMap<String,Object> result = new HashMap<String,Object>();
		
		ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) param.get("list");
		//본인이 선택한 보기들을 리스트로 얻음
		list = (ArrayList<HashMap<String, String>>) problemDAO.getChoicesInfo(param);
		
		List right_problems = new ArrayList();
		List wrong_problems = new ArrayList();
		
		//채점함
		int right = 0, wrong = 0;
		for(int i=0;i<list.size();i++) {
			if(list.get(i).get("choice_yn").equalsIgnoreCase("Y")) {
				right++;
				right_problems.add(list.get(i).get("problem_id"));
			}else {
				wrong++;
				wrong_problems.add(list.get(i).get("problem_id"));
			}
		}
		
		problemDAO.updateChoiceCounts(param);
		
		problemDAO.insertRecords(param);
		
		//채점결과 반환
		result.put("percentage", (right*1.0/(right+wrong)*1.0)*100);
		result.put("right_score", right);
		result.put("wrong_score", wrong);
		result.put("right_problems", right_problems);
		result.put("wrong_problems", wrong_problems);
		return result;
	}
	
	public List getCategories(HashMap param){
		return problemDAO.getCategories(param);
	}
}