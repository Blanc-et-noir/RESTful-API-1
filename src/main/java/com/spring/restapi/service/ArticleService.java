package com.spring.restapi.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

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
	private static String IMAGE_BASE_PATH = ".//restapi_files//article_images//";
	
	private static String getExtension(MultipartFile mf) {
		String[] str = mf.getOriginalFilename().split("\\.");
		return str[str.length-1];
	}
	
	public void addArticle(String contextPath, MultipartRequest mRequest,HashMap param) throws FailedToAddArticleException, Exception {		
		
		Iterator<String> itor =  mRequest.getFileNames();
				
		articleDAO.addArticle(param);
		
		Queue<HashMap> list = new LinkedList<HashMap>();
		
		while(itor.hasNext()) {
			String filename = itor.next();
			MultipartFile mf = mRequest.getFile(filename);
			
			HashMap hm = new HashMap();
			hm.put("article_image_file", mf);
			hm.put("article_image_id", UUID.randomUUID().toString());
			
			list.add(hm);
		}
		
		param.put("article_images", list);
		
		if(!list.isEmpty()) {
			articleDAO.addArticleImages(param);
		}
			
		while(!list.isEmpty()) {
			HashMap hm = list.poll();
			MultipartFile  mf = (MultipartFile) hm.get("article_image_file");
			String article_image_id = (String) hm.get("article_image_id");
			String article_id = (String) param.get("article_id");
			File file = new File(contextPath+IMAGE_BASE_PATH+article_id+"//"+article_image_id+"."+getExtension(mf));
			
			System.out.println(file.getAbsolutePath());
			
			if(!file.exists()) {
				file.mkdirs();
			}
			
			mf.transferTo(file);
		}
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
