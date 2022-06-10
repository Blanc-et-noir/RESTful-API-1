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
	
	//카테고리 ID에 해당하는 문제를 limit 개수만큼 발급하여, 문제 ID, 문제 내용, 문제에 첨부된 이미지, 보기 번호, 정답 번호, 정답률 등등을 반환함.
	@RequestMapping(value= {"/problems"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getProblems(@RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 특정 카테고리에 대한 문제를 limit 개수만큼 반환함.
		try {
			//2. 전달받은 limit가 0이하의 정수 또는 숫자가 아닌 경우, 기본적으로 20개의 문제를 발급함.
			try {
				if(Integer.parseInt((String) param.get("limit"))<=0) {
					param.put("limit", 20);
				}
			}catch(Exception e2) {
				param.put("limit", 20);
			}
			
			//3. 조건에 해당하는 문제를 limit 개수만큼 얻음.
			result.put("problems",problemService.getProblems(param));
			result.put("flag", true);
			result.put("content", "문제 정보 획득 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "문제 정보 획득 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//문제에 대해 새로운 의견을 작성함.
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.POST})
	public ResponseEntity<HashMap> writeOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 사용자로부터 받은 내용을 바탕으로 새로 의견을 작성함.
		try {
			//2. 토큰으로부터 사용자 ID를 얻음.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			
			//3. 문제에 대한 새로운 의견을 작성함.
			problemService.writeOpinion(param);
			
			result.put("flag", true);
			result.put("content", "댓글 등록 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
		//4. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "댓글 등록 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//해당 문제에 대한 의견을 삭제함.
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.DELETE})
	public ResponseEntity<HashMap> deleteOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 사용자로부터 문제 ID와 의견 ID를 전달받아 해당 의견을 삭제함.
		try {
			//2. 자신이 작성한 의견인지 판단하기위해 토큰으로부터 사용자 ID를 얻음.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			
			//3. 해당 의견을 삭제함.
			problemService.deleteOpinion(param);
			
			result.put("flag", true);
			result.put("content", "댓글 삭제 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. 유효하지 않은 문제 ID인 경우 InvalidProblemIdException
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//5. 유효하지 않은 의견 ID인 경우 InvalidOpinionIdException
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. 존재하지 않는 사용자 ID인 경우 InvalidUserIdException
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//7. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "댓글 삭제 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//문제에 작성된 의견을 수정함.
	@RequestMapping(value= {"/problems/{problem_id}/opinions/{opinion_id}"}, method= {RequestMethod.PUT})
	public ResponseEntity<HashMap> updateOpinion(@PathVariable("problem_id") String problem_id,@PathVariable("opinion_id") String opinion_id, @RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 사용자로부터 전달받은 내용으로 해당 의견을 수정함.
		try {
			//2. 토큰으로부터 사용자 ID를 얻어 해당 의견을 수정할 수 있는지 없는지 판단함.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			param.put("opinion_id", opinion_id);
			
			//3. 해당 의견을 수정함.
			problemService.updateOpinion(param);
			
			result.put("flag", true);
			result.put("content", "댓글 수정 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//4. 문제 ID에 해당하는 문제정보가 존재하지 않는경우 InvalidProblemIdException
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//5. 해당 문제에 존재하는 의견 ID에 해당하는 의견정보가 존재하지 않는 경우 InvalidOpinionIdException
		}catch(InvalidOpinionIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. 사용자 ID가 존재하지 않는 경우 InvalidUserIdException
		}catch(InvalidUserIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//7. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "댓글 수정 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//해당 문제에 대한 사용자들의 의견을 페이징하여 반환함.
	@RequestMapping(value= {"/problems/{problem_id}/opinions"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> readOpinion(@PathVariable("problem_id") String problem_id, @RequestParam HashMap<String,String> param, HttpServletRequest request){
		HashMap result = new HashMap();
		//1. 문제 번호, section, page값을 전달받아 이에 해당하는 의견을 페이징하여 전달함.
		try {
			//2. 수정 가능 여부 판단을 위해 사용자 ID를 얻음.
			param.put("user_id", JwtUtil.getData(CookieUtil.getAccesstoken(request), "user_id"));
			param.put("problem_id", problem_id);
			
			//3. 클라이언트로부터 전달받은 section, page값을 적절히 계산하여 페이징 offset를 구함.
			int section = Integer.parseInt(param.get("section"));
			int page = Integer.parseInt(param.get("page"));
			
			param.put("offset", ((section-1)*25+(page-1)*5)+"");

			//4. 해당 offset에 해당하는 문제 의견 최대 5개를 얻음.
			result = problemService.readOpinions(param);
			result.put("flag", true);
			result.put("content", "댓글 읽기 성공");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//5. 해당 문제 ID정보를 갖는 문제가 존재하지 않는 경우.
		}catch(InvalidProblemIdException e) {
			result.put("flag", false);
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		//6. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", false);
			result.put("content", "댓글 읽기 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	//문제 ID와 이미지 ID를 전달받아 해당 문제에 첨부된 이미지를 반환함.
	@RequestMapping(value= {"/problems/{problem_id}/images/{problem_image_name}"}, method= {RequestMethod.GET})
	public void getImage(@PathVariable("problem_id") String problem_id,@PathVariable("problem_image_name") String problem_image_id, HttpServletRequest request, HttpServletResponse response){
		HashMap result = new HashMap();
		//1. 문제 ID, 이미지 ID정보가 모두 정확하다면 해당 이미지를 반환함.
		try {
			//2. 첨부된 이미지 파일의 경로를 설정함.
			String extension = "png";
			String path = request.getServletContext().getRealPath("") + IMAGE_BASE_PATH+problem_id+"//"+problem_image_id+"."+extension;
			
			//3. 해당 경로를 이용해 파일 객체를 생성함.
			File file = new File(path);
			
			//4. 존재하지 않는 파일이라면 아무것도 반환하지 않음.
			if(!file.exists()) {
				result.put("flag", false);
				result.put("content", "이미지 파일이 존재하지 않음");
				return;
			//5. 존재하는 파일이라면 아래의 작업을 수행함.
			}else {
				response.setHeader("Cache-Control", "no-cache");
				response.addHeader("Content-disposition", "attachment; fileName="+problem_image_id+"."+extension);
				
				//6. 해당 파일을 읽을때 사용할 입력스트림, 내보낼 출력스트림을 얻음.
				FileInputStream in = new FileInputStream(file);
				OutputStream out = response.getOutputStream();
				
				byte[] buffer = new byte[1024*1024*10];
				
				//7. 파일을 매번 버퍼의 크기만큼 읽고, 이를 출력스트림으로 내보냄.
				while(true) {
					int len = in.read(buffer);
					if(len==-1) {
						break;
					}
					out.write(buffer, 0, len);
				}
				
				//8. 입, 출력 스트림을 닫음.
				in.close();
				out.close();
			}
			
			result.put("flag", true);
			result.put("content", "이미지 파일 로드 성공");
			return;
		//9. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "이미지 파일 로드 실패");
			return;
		}
	}
	
	//문제의 카테고리들 목록을 발급함.
	@RequestMapping(value= {"/problems/categories"}, method= {RequestMethod.GET})
	public ResponseEntity<HashMap> getCategories(@RequestParam HashMap param){
		HashMap result = new HashMap();
		//1. 카테고리 ID와 카테고리 이름들의 리스트를 반환함.
		try {
			result.put("flag", true);
			result.put("content", "문제 종류 획득 성공");
			result.put("categories", problemService.getCategories(param));
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		//2. 기타 예외가 발생한 경우.
		}catch(Exception e) {
			result.put("flag", false);
			result.put("content", "문제 종류 획득 실패");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}