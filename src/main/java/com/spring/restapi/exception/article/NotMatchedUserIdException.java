package com.spring.restapi.exception.article;

public class NotMatchedUserIdException extends Exception{
	public NotMatchedUserIdException(){
		super("������ �ۼ��� �Խñ��� �ƴմϴ�.");
	}
	public NotMatchedUserIdException(String message){
		super(message);
	}
}
