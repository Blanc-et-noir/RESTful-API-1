package com.spring.restapi.exception.article;

public class NotFoundArticleException extends Exception{
	public NotFoundArticleException(){
		super("�Խñ��� �������� �ʽ��ϴ�.");
	}
	public NotFoundArticleException(String message){
		super(message);
	}
}
