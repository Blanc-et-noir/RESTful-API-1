package com.spring.restapi.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.restapi.exception.article.FailedToAddArticleException;
import com.spring.restapi.service.ArticleService;

@Controller("articleController")
public class ArticleController {

	@Autowired
	private ArticleService articleService;
	
	
	@RequestMapping("/article/addArticle.do")
	public ResponseEntity<HashMap> addArticle(@RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			articleService.addArticle(request, param);
			result.put("flag", "true");
			result.put("content", "게시글 작성에 성공했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(FailedToAddArticleException e) {
			result.put("flag", "false");
			result.put("content", e.getMessage());
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			result.put("flag", "false");
			result.put("content", "게시글 작성중 알 수 없는 오류가 발생했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping("/article/listArticles.do")
	public ResponseEntity<HashMap> listArticles(@RequestParam HashMap param, HttpServletRequest request){
		HashMap result = new HashMap();
		try {
			List list = articleService.listArticles(request, param);
			result.put("flag", "true");
			result.put("content", "게시글 목록 조회에 성공했습니다.");
			result.put("list", list);
			return new ResponseEntity<HashMap>(result,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("flag", "false");
			result.put("content", "게시글 목록 조회중 알 수 없는 오류가 발생했습니다.");
			return new ResponseEntity<HashMap>(result,HttpStatus.BAD_REQUEST);
		}
	}
}
