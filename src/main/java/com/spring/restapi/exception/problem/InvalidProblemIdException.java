package com.spring.restapi.exception.problem;

public class InvalidProblemIdException extends Exception{
	public InvalidProblemIdException(){
		super("해당 문제 정보는 존재하지 않습니다.");
	}
	public InvalidProblemIdException(String message){
		super(message);
	}
}
