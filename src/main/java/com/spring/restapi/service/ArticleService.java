package com.spring.restapi.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.restapi.dao.ArticleDAO;
import com.spring.restapi.exception.article.FailedToAddArticleException;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		FailedToAddArticleException.class,
		Exception.class
		}
)
@Service("articleService")
public class ArticleService {
	@Autowired
	private ArticleDAO articleDAO;

	public void addArticle(MultipartRequest mRequest,HashMap<String,String> param) throws FailedToAddArticleException, Exception {		
		
		Iterator<String> itor =  mRequest.getFileNames();
				
		//articleDAO.addArticle(param);
		
		while(itor.hasNext()) {
			String filename = itor.next();
			System.out.println(mRequest.getFile(filename).getOriginalFilename());
		}
		
		//articleDAO.addArticleImages(param);
	}
	
	public HashMap getArticles(HashMap param){
		HashMap result = new HashMap();
		result.put("articles_total", articleDAO.getArticlesTotal(param));
		result.put("articles", articleDAO.getArticles(param));
		return result;
	}
	
	public HashMap getArticle(HashMap param) {
		HashMap result = new HashMap();
		result.put("article", articleDAO.getArticle(param));
		articleDAO.increaseArticleView(param);
		result.put("article_images", articleDAO.getArticleImages(param));
		return result;
	}
}
