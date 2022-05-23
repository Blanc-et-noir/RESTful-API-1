package com.spring.restapi.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.ArticleDAO;
import com.spring.restapi.exception.article.FailedToAddArticleException;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		FailedToAddArticleException.class,
		Exception.class
		}
)
@Service("articleService")
public class ArticleService {
	@Autowired
	private ArticleDAO articleDAO;

	public void addArticle(HttpServletRequest request,HashMap<String,String> param) throws FailedToAddArticleException, Exception {
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_id = JwtUtil.getData(user_accesstoken, "user_id");
		
		param.put("user_id", user_id);
		
		articleDAO.addArticle(param);
		
	}
	
	public List listArticles(HttpServletRequest request,HashMap<String,String> param){
		return articleDAO.listArticles(param);
	}
}
