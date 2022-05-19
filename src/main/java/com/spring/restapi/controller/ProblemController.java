package com.spring.restapi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.exception.problem.InvalidOpinionIdException;
import com.spring.restapi.exception.problem.InvalidProblemIdException;
import com.spring.restapi.exception.problem.InvalidUserIdException;
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
	
	private static final String IMAGE_BASE_PATH = "//home//tomcat9//webapps//restapi_files//problem_images//"; 
	
	//�׼��� �ʿ�
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
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> writeOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			param.put("user_id", jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			problemService.writeOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ��� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ��� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.DELETE})
	public ResponseEntity<HashMap> deleteOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			param.put("user_id", jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			problemService.deleteOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.PUT})
	public ResponseEntity<HashMap> updateOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			param.put("user_id", jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			
			problemService.updateOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> readOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			param.put("user_id", jwtUtil.getData(cookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			
			result = problemService.readOpinions(param);
			result.put("flag", true);
			result.put("content", "��� �б� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "��� �б� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
	@RequestMapping(value= {"/problems/{problem_id}/images/{problem_image_name}"}, method= {RequestMethod.GET})
	public void getImage(@PathVariable("problem_id") String problem_id,@PathVariable("problem_image_name") String problem_image_name, HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			String extension = "png";
			String path = IMAGE_BASE_PATH+problem_id+"//"+problem_image_name+"."+extension;
			
			File file = new File(path);
			
			if(!file.exists()) {
				result.put("flag", false);
				result.put("content", "�̹��� ������ �������� ����");
				return;
			}else {
				response.setHeader("Cache-Control", "no-cache");
				response.addHeader("Content-disposition", "attachment; fileName="+problem_image_name+"."+extension);		
				FileInputStream in = new FileInputStream(file);
				
				byte[] buffer = new byte[1024*1024*10];
				OutputStream out = response.getOutputStream();
				while(true) {
					int len = in.read(buffer);
					if(len==-1) {
						break;
					}
					out.write(buffer, 0, len);
				}
				in.close();
				out.close();
			}
			result.put("flag", true);
			result.put("content", "�̹��� ���� �ε� ����");
			return;
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "�̹��� ���� �ε� ����");
			return;
		}
	}
	
	//��ū�ʿ����
	@RequestMapping(value= {"/categories"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getCategories(@RequestParam HashMap param){
		HashMap result = new HashMap();
		try {
			result.put("flag", true);
			result.put("content", "���� ���� ȹ�� ����");
			result.put("categories", problemService.getCategories(param));
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�׼��� �ʿ�
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
			result.put("content", "ä�� ����");
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
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "ä�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}