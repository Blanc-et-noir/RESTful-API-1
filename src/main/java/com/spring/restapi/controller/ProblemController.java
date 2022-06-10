package com.spring.restapi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.exception.problem.InvalidOpinionIdException;
import com.spring.restapi.exception.problem.InvalidProblemIdException;
import com.spring.restapi.exception.problem.InvalidUserIdException;
import com.spring.restapi.service.ProblemService;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;

@Controller("problemController")
public class ProblemController {
	@Autowired
	ProblemService problemService;
	
	private static final String IMAGE_BASE_PATH = ".//restapi_files//problem_images//"; 
	
	//ī�װ� ID�� �ش��ϴ� ������ limit ������ŭ �߱��Ͽ�, ���� ID, ���� ����, ������ ÷�ε� �̹���, ���� ��ȣ, ���� ��ȣ, ����� ����� ��ȯ��.
	@RequestMapping(value= {"/problems"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getProblems(@RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. Ư�� ī�װ��� ���� ������ limit ������ŭ ��ȯ��.
		try {
			//2. ���޹��� limit�� 0������ ���� �Ǵ� ���ڰ� �ƴ� ���, �⺻������ 20���� ������ �߱���.
			try {
				if(Integer.parseInt((String) param.get("limit"))<=0) {
					param.put("limit", 20);
				}
			}catch(Exception e2) {
				param.put("limit", 20);
			}
			
			//3. ���ǿ� �ش��ϴ� ������ limit ������ŭ ����.
			result.put("problems",problemService.getProblems(param));
			result.put("flag", true);
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//������ ���� ���ο� �ǰ��� �ۼ���.
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> writeOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ����ڷκ��� ���� ������ �������� ���� �ǰ��� �ۼ���.
		try {
			//2. ��ū���κ��� ����� ID�� ����.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			
			//3. ������ ���� ���ο� �ǰ��� �ۼ���.
			problemService.writeOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ��� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//4. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ��� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�ش� ������ ���� �ǰ��� ������.
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.DELETE})
	public ResponseEntity<HashMap> deleteOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ����ڷκ��� ���� ID�� �ǰ� ID�� ���޹޾� �ش� �ǰ��� ������.
		try {
			//2. �ڽ��� �ۼ��� �ǰ����� �Ǵ��ϱ����� ��ū���κ��� ����� ID�� ����.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			
			//3. �ش� �ǰ��� ������.
			problemService.deleteOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. ��ȿ���� ���� ���� ID�� ��� InvalidProblemIdException
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//5. ��ȿ���� ���� �ǰ� ID�� ��� InvalidOpinionIdException
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. �������� �ʴ� ����� ID�� ��� InvalidUserIdException
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//7. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//������ �ۼ��� �ǰ��� ������.
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.PUT})
	public ResponseEntity<HashMap> updateOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ����ڷκ��� ���޹��� �������� �ش� �ǰ��� ������.
		try {
			//2. ��ū���κ��� ����� ID�� ��� �ش� �ǰ��� ������ �� �ִ��� ������ �Ǵ���.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			
			//3. �ش� �ǰ��� ������.
			problemService.updateOpinion(param);
			
			result.put("flag", true);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. ���� ID�� �ش��ϴ� ���������� �������� �ʴ°�� InvalidProblemIdException
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//5. �ش� ������ �����ϴ� �ǰ� ID�� �ش��ϴ� �ǰ������� �������� �ʴ� ��� InvalidOpinionIdException
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. ����� ID�� �������� �ʴ� ��� InvalidUserIdException
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//7. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "��� ���� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//�ش� ������ ���� ����ڵ��� �ǰ��� ����¡�Ͽ� ��ȯ��.
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> readOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap<String,String> param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. ���� ��ȣ, section, page���� ���޹޾� �̿� �ش��ϴ� �ǰ��� ����¡�Ͽ� ������.
		try {
			//2. ���� ���� ���� �Ǵ��� ���� ����� ID�� ����.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			
			//3. Ŭ���̾�Ʈ�κ��� ���޹��� section, page���� ������ ����Ͽ� ����¡ offset�� ����.
			int section = Integer.parseInt(param.get("section"));
			int page = Integer.parseInt(param.get("page"));
			
			param.put("offset", ((section-1)*25+(page-1)*5)+"");

			//4. �ش� offset�� �ش��ϴ� ���� �ǰ� �ִ� 5���� ����.
			result = problemService.readOpinions(param);
			result.put("flag", true);
			result.put("content", "��� �б� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. �ش� ���� ID������ ���� ������ �������� �ʴ� ���.
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "��� �б� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//���� ID�� �̹��� ID�� ���޹޾� �ش� ������ ÷�ε� �̹����� ��ȯ��.
	@RequestMapping(value= {"/problems/{problem_id}/images/{problem_image_name}"}, method= {RequestMethod.GET})
	public void getImage(@PathVariable("problem_id") String problem_id,@PathVariable("problem_image_name") String problem_image_id, HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. ���� ID, �̹��� ID������ ��� ��Ȯ�ϴٸ� �ش� �̹����� ��ȯ��.
		try {
			//2. ÷�ε� �̹��� ������ ��θ� ������.
			String extension = "png";
			String path = request.getServletContext().getRealPath("") + IMAGE_BASE_PATH+problem_id+"//"+problem_image_id+"."+extension;
			
			//3. �ش� ��θ� �̿��� ���� ��ü�� ������.
			File file = new File(path);
			
			//4. �������� �ʴ� �����̶�� �ƹ��͵� ��ȯ���� ����.
			if(!file.exists()) {
				result.put("flag", false);
				result.put("content", "�̹��� ������ �������� ����");
				return;
			//5. �����ϴ� �����̶�� �Ʒ��� �۾��� ������.
			}else {
				response.setHeader("Cache-Control", "no-cache");
				response.addHeader("Content-disposition", "attachment; fileName="+problem_image_id+"."+extension);
				
				//6. �ش� ������ ������ ����� �Է½�Ʈ��, ������ ��½�Ʈ���� ����.
				FileInputStream in = new FileInputStream(file);
				OutputStream out = response.getOutputStream();
				
				byte[] buffer = new byte[1024*1024*10];
				
				//7. ������ �Ź� ������ ũ�⸸ŭ �а�, �̸� ��½�Ʈ������ ������.
				while(true) {
					int len = in.read(buffer);
					if(len==-1) {
						break;
					}
					out.write(buffer, 0, len);
				}
				
				//8. ��, ��� ��Ʈ���� ����.
				in.close();
				out.close();
			}
			
			result.put("flag", true);
			result.put("content", "�̹��� ���� �ε� ����");
			return;
		//9. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "�̹��� ���� �ε� ����");
			return;
		}
	}
	
	//������ ī�װ��� ����� �߱���.
	@RequestMapping(value= {"/problems/categories"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getCategories(@RequestParam HashMap param){
		HashMap result = new HashMap();
		//1. ī�װ� ID�� ī�װ� �̸����� ����Ʈ�� ��ȯ��.
		try {
			result.put("flag", true);
			result.put("content", "���� ���� ȹ�� ����");
			result.put("categories", problemService.getCategories(param));
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "���� ���� ȹ�� ����");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}