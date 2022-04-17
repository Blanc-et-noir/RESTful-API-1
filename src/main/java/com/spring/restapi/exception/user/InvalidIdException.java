package com.spring.restapi.exception.user;

public class InvalidIdException extends Exception{
	public InvalidIdException(){
		super("가입되지 않은 회원정보입니다.");
	}
	public InvalidIdException(String message){
		super(message);
	}
}