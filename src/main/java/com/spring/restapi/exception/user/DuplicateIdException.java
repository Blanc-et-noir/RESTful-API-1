package com.spring.restapi.exception.user;

public class DuplicateIdException extends Exception{
	public DuplicateIdException(){
		super("�̹� ���Ե� ���̵� �����Դϴ�.");
	}
	public DuplicateIdException(String message){
		super(message);
	}
}