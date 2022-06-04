package com.spring.restapi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private static final String IMAGE_BASE_PATH = "restapi_files\\article_images\\";
	
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
			hm.put("article_image_extension", getExtension(mf));
			
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
			File file = new File(contextPath+IMAGE_BASE_PATH+article_id+"\\"+article_image_id+"."+getExtension(mf));
			
			System.out.println(file.getAbsolutePath());
			
			if(!file.exists()) {
				file.mkdirs();
			}
			
			mf.transferTo(file);
		}
	}
	
	public void modifyArticle(String path, MultipartRequest mRequest,HttpServletRequest request, HashMap param) {
		Iterator<String> itor = mRequest.getFileNames();
		
		//1. ������ �׼�����ū���� �Խñ� ������ �������� �ľ�
		
		//2. ������ �����ϴٸ� �Խñ� ����, ���� ����
		
		//3. �Խñ� ������ ���������� ���� �߰��� ����, ������ ���� ������ ��ó����  �Խñ� �̹��� ���� ���� ����
		
		while(itor.hasNext()) {
			MultipartFile mf = mRequest.getFile(itor.next());
			System.out.println("�����߰��� ���� �̸� : "+mf.getOriginalFilename());
		}
		
		String[] str = request.getParameterValues("removed_file_ids");

		if(str !=null) {
			for(String s : str) {
				System.out.println("���������� id : "+ s);
			}
		}
		
		//4. DB������ �Ϸ������, �ռ� ��ó���� ������ ������ ������ ������ ���� ���� �� ���ε��� ���� ���ε�
	}
	
	public HashMap getArticles(HashMap param){
		HashMap result = new HashMap();
		result.put("articles_total", articleDAO.getArticlesTotal(param));
		result.put("articles", articleDAO.getArticles(param));
		return result;
	}
	
	public HashMap getArticle(HashMap param) {
		HashMap result = new HashMap();
		HashMap article = articleDAO.getArticle(param);
		
		articleDAO.increaseArticleView(param);
		article.put("article_images", articleDAO.getArticleImages(param));
		
		result.put("article", article);
		return result;
	}
	
	public void getArticleImage(HttpServletRequest request,HttpServletResponse response, HashMap param) throws IOException{
		int len;
		String path = request.getServletContext().getRealPath("") + IMAGE_BASE_PATH+param.get("article_id")+"\\"+param.get("article_image_id")+"."+param.get("article_image_extension");
		File file = new File(path);
		System.out.println(file.exists());
		System.out.println(param.get("article_image_extension"));
		System.out.println(path);
		if(!file.exists()) {
			return;
		}else {
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Content-disposition", "attachment; fileName="+param.get("article_image_id")+"."+param.get("article_image_extension"));
			
			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(file);
			
			byte[] buffer = new byte[1024*1024*10];
			
			while((len = in.read(buffer))!=-1) {
				out.write(buffer, 0, len);
			}
			
			in.close();
			out.close();
			return;
		}
	}
}
