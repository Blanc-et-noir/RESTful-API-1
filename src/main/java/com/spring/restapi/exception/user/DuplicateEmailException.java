package com.spring.restapi.exception.user;

public class DuplicateEmailException extends Exception{
	public DuplicateEmailException(){
		super("�̹� ���Ե� �̸��� �����Դϴ�.");
	}
	public DuplicateEmailException(String message){
		super(message);
	}
}