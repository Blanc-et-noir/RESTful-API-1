package com.spring.restapi.exception.user;

public class UnableToInsertRecordsException extends Exception{
	public UnableToInsertRecordsException(){
		super("���� Ƚ�� ���ſ� �����߽��ϴ�.");
	}
	public UnableToInsertRecordsException(String message){
		super(message);
	}
}