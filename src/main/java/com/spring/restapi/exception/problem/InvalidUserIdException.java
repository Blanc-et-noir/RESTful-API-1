package com.spring.restapi.exception.problem;

public class InvalidUserIdException extends Exception{
	public InvalidUserIdException(){
		super("������ �ۼ��� ����� �ƴմϴ�.");
	}
	public InvalidUserIdException(String message){
		super(message);
	}
}
