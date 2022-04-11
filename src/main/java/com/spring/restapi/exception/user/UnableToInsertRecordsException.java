package com.spring.restapi.exception.user;

public class UnableToInsertRecordsException extends Exception{
	public UnableToInsertRecordsException(){
		super("선택 횟수 갱신에 실패했습니다.");
	}
	public UnableToInsertRecordsException(String message){
		super(message);
	}
}