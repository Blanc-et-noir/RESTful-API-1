package com.spring.restapi.exception.problem;

public class InvalidProblemIdException extends Exception{
	public InvalidProblemIdException(){
		super("�ش� ���� ������ �������� �ʽ��ϴ�.");
	}
	public InvalidProblemIdException(String message){
		super(message);
	}
}
