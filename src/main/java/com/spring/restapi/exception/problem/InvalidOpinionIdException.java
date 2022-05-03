package com.spring.restapi.exception.problem;

public class InvalidOpinionIdException extends Exception{
	public InvalidOpinionIdException(){
		super("해당 댓글 정보는 존재하지 않습니다.");
	}
	public InvalidOpinionIdException(String message){
		super(message);
	}
}
