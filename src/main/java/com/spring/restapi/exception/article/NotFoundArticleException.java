package com.spring.restapi.exception.article;

public class NotFoundArticleException extends Exception{
	public NotFoundArticleException(){
		super("게시글이 존재하지 않습니다.");
	}
	public NotFoundArticleException(String message){
		super(message);
	}
}
