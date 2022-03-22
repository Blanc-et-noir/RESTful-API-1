package com.spring.restapi.exception.article;

public class FailedToAddArticleException extends Exception{
	public FailedToAddArticleException(){
		super("게시글 작성에 실패했습니다.");
	}
	public FailedToAddArticleException(String message){
		super(message);
	}
}