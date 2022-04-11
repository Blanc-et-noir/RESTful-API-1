package com.spring.restapi.exception.user;

public class UnableToUpdateCountsException extends Exception{
	public UnableToUpdateCountsException(){
		super("채점기록 등록에 실패했습니다.");
	}
	public UnableToUpdateCountsException(String message){
		super(message);
	}
}