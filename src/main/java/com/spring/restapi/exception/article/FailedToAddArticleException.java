package com.spring.restapi.exception.article;

public class FailedToAddArticleException extends Exception{
	public FailedToAddArticleException(){
		super("�Խñ� �ۼ��� �����߽��ϴ�.");
	}
	public FailedToAddArticleException(String message){
		super(message);
	}
}