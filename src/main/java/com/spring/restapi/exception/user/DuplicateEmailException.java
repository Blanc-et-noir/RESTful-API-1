package com.spring.restapi.exception.user;

public class DuplicateEmailException extends Exception{
	public DuplicateEmailException(){
		super("이미 가입된 이메일 정보입니다.");
	}
	public DuplicateEmailException(String message){
		super(message);
	}
}