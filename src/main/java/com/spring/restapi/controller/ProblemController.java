package com.spring.restapi.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.exception.user.UnableToInsertRecordsException;
import com.spring.restapi.exception.user.UnableToUpdateCountsException;
import com.spring.restapi.service.ProblemService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;

@Controller("problemController")
public class ProblemController {
	@Autowired
	ProblemService problemService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CookieUtil cookieUtil;
	
	//액세스 필요
	@RequestMapping(value= {"/problems"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getProblems(@RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			try {
				if(Integer.parseInt((String) param.get("limit"))<=0) {
					param.put("limit", 20);
				}
			}catch(Exception e2) {
				param.put("limit", 20);
			}
			result.put("problems",problemService.getProblems(param));
			result.put("flag", true);
			result.put("content", "문제 정보 획득 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "문제 정보 획득 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//토큰필요없음
	@RequestMapping(value= {"/categories"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getCategories(@RequestParam HashMap param){
		HashMap result = new HashMap();
		try {
			result.put("flag", true);
			result.put("content", "문제 정보 획득 성공");
			result.put("categories", problemService.getCategories(param));
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "문제 정보 획득 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//액세스 필요
	@RequestMapping(value= {"/scores"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> scoreProblems(@RequestBody HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			String user_id = jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id");
			ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) param.get("list");
			for(int i=0;i<list.size();i++) {
				list.get(i).put("user_id", user_id);
			}
			result = problemService.scoreProblems(param);
			result.put("flag", true);
			result.put("content", "채점 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		}catch(UnableToUpdateCountsException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(UnableToInsertRecordsException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "채점 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}