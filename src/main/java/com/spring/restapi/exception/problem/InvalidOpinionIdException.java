package com.spring.restapi.exception.problem;

public class InvalidOpinionIdException extends Exception{
	public InvalidOpinionIdException(){
		super("�ش� ��� ������ �������� �ʽ��ϴ�.");
	}
	public InvalidOpinionIdException(String message){
		super(message);
	}
}
