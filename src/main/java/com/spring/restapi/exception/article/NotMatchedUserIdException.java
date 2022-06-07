package com.spring.restapi.exception.article;

public class NotMatchedUserIdException extends Exception{
	public NotMatchedUserIdException(){
		super("본인이 작성한 게시글이 아닙니다.");
	}
	public NotMatchedUserIdException(String message){
		super(message);
	}
}
