package com.spring.restapi.exception.user;

public class DuplicateIdException extends Exception{
	public DuplicateIdException(){
		super("이미 가입된 아이디 정보입니다.");
	}
	public DuplicateIdException(String message){
		super(message);
	}
}