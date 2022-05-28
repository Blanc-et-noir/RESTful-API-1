package com.spring.restapi.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.restapi.exception.article.FailedToAddArticleException;
import com.spring.restapi.service.ArticleService;

@Controller("articleController")
public class ArticleController {
	@Autowired
	private ArticleService articleService;
	
	@RequestMapping(value={"/articles"},method={RequestMethod.POST})
	public ResponseEntity<HashMap> addArticle(MultipartRequest mRequest, HttpServletRequest request){
		return null;
	}
	
	@RequestMapping(value={"/articles"},method={RequestMethod.GET})
	public ResponseEntity<HashMap> getArticles(MultipartRequest mRequest, HttpServletRequest request){
		return null;
	}
	
	@RequestMapping(value={"/articles/{article_id}"},method={RequestMethod.DELETE})
	public ResponseEntity<HashMap> deleteArticle(MultipartRequest mRequest, HttpServletRequest request){
		return null;
	}
	
	@RequestMapping(value={"/articles/{article_id}"},method={RequestMethod.PUT})
	public ResponseEntity<HashMap> updateArticle(MultipartRequest mRequest, HttpServletRequest request){
		return null;
	}
	
	@RequestMapping(value={"/articles/{article_id}"},method={RequestMethod.GET})
	public ResponseEntity<HashMap> getArticle(MultipartRequest mRequest, HttpServletRequest request){
		return null;
	}
}