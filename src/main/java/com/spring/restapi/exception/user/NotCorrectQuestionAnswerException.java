package com.spring.restapi.exception.user;

public class NotCorrectQuestionAnswerException extends Exception{
	public NotCorrectQuestionAnswerException(){
		super("비밀번호 찾기 질문에 대한 답이 일치하지 않습니다.");
	}
	public NotCorrectQuestionAnswerException(String message){
		super(message);
	}
}