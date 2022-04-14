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
	
	private static final String IMAGE_BASE_PATH = "D:\\RestAPI\\problems\\"; 
	
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
	@RequestMapping(value= {"/problems/{problem_id}/images/{problem_image_name}"}, method= {RequestMethod.GET})
	public void getImage(@PathVariable("problem_id") int problem_id,@PathVariable("problem_image_name") String problem_image_name, HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		try {
			//param���� ������ȣ, �̹��� �̸� �ʿ�
			
			//String extension = request.getHeader("accept").split("/")[1];
			String extension = "png";
			String path = IMAGE_BASE_PATH+problem_id+"\\images\\"+problem_image_name+"."+extension;
			
			File file = new File(path);
			System.out.println(path);
			if(!file.exists()) {
				System.out.println("����");
				result.put("flag", false);
				result.put("content", "�̹��� ������ �������� ����");
				return;
				//return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
			}else {
				System.out.println("����:"+file.length());
				response.setHeader("Cache-Control", "no-cache");
				response.addHeader("Content-disposition", "attachment; fileName="+problem_image_name+"."+extension);		
				FileInputStream in = new FileInputStream(file);
				
				byte[] buffer = new byte[1024*1024*10];
				OutputStream out = response.getOutputStream();
				while(true) {
					int len = in.read(buffer);
					System.out.println(len);
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
			//return new ResponseEntity<HashMap>(result,HttpStatus.OK);
			return;
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "�̹��� ���� �ε� ����");
			//return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
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
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(UnableToInsertRecordsException e) {
			e.printStackTrace();
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