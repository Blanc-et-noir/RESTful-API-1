package com.spring.restapi.exception.user;

public class NotCorrectQuestionAnswerException extends Exception{
	public NotCorrectQuestionAnswerException(){
		super("��й�ȣ ã�� ������ ���� ���� ��ġ���� �ʽ��ϴ�.");
	}
	public NotCorrectQuestionAnswerException(String message){
		super(message);
	}
}