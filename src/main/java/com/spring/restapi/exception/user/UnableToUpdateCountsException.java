package com.spring.restapi.exception.user;

public class UnableToUpdateCountsException extends Exception{
	public UnableToUpdateCountsException(){
		super("ä����� ��Ͽ� �����߽��ϴ�.");
	}
	public UnableToUpdateCountsException(String message){
		super(message);
	}
}