package com.spring.restapi.exception.problem;

public class InvalidUserIdException extends Exception{
	public InvalidUserIdException(){
		super("본인이 작성한 댓글이 아닙니다.");
	}
	public InvalidUserIdException(String message){
		super(message);
	}
}
