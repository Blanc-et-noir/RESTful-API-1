package com.spring.restapi.exception.user;

public class InvalidPwException extends Exception{
	public InvalidPwException(){
		super("���Ե��� ���� ȸ�������Դϴ�.");
	}
	public InvalidPwException(String message){
		super(message);
	}
}