package com.spring.restapi.exception.user;

public class InvalidIdException extends Exception{
	public InvalidIdException(){
		super("���Ե��� ���� ID �����Դϴ�.");
	}
	public InvalidIdException(String message){
		super(message);
	}
}