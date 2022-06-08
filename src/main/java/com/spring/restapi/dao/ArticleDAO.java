package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("articleDAO")
public class ArticleDAO {
	@Autowired
	private SqlSession sqlSession;
	
	public void addArticle(HashMap<String,String> param){
		sqlSession.insert("article.addArticle",param);
	}
	
	public int getArticlesTotal(HashMap<String,String> param){
		return sqlSession.selectOne("article.getArticlesTotal", param);
	}
	
	public List getArticles(HashMap<String,String> param){
		return sqlSession.selectList("article.getArticles", param);
	}
	
	public List getArticleImages(HashMap<String,String> param){
		return sqlSession.selectList("article.getArticleImages", param);
	}
	
	public HashMap getArticle(HashMap<String,String> param){
		return sqlSession.selectOne("article.getArticle", param);
	}
	
	public void increaseArticleView(HashMap<String,String> param) {
		sqlSession.update("article.increaseArticleView", param);
	}
	
	public void addArticleImages(HashMap param) {
		sqlSession.insert("article.addArticleImages", param);
	}
	
	
	public void isEditableArticle(HashMap param) throws Exception {
		if(sqlSession.selectOne("article.isEditableArticle",param) == null) {
			throw new Exception();
		}
	}
	
	public void updateArticle(HashMap<String,String> param){
		sqlSession.update("article.updateArticle", param);
	}
	
	public void insertArticleImages(HashMap param){
		sqlSession.insert("article.insertArticleImages", param);
	}
	
	public void deleteArticleImages(HashMap param){
		sqlSession.delete("article.deleteArticleImages", param);
	}

	/* 아직 댓글기능은 구현하지 않음
	public void deleteAllComments(HashMap<String, String> param) {
		sqlSession.delete("article.deleteAllComments",param);
	}
	*/
	
	public void deleteArticle(HashMap<String, String> param) {
		sqlSession.delete("article.deleteArticle",param);
	}

	public void deleteAllArticleImages(HashMap<String, String> param) {
		sqlSession.delete("article.deleteAllArticleImages",param);
	}
}