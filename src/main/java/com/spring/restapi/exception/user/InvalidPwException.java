package com.spring.restapi.exception.user;

public class InvalidPwException extends Exception{
	public InvalidPwException(){
		super("가입되지 않은 회원정보입니다.");
	}
	public InvalidPwException(String message){
		super(message);
	}
}