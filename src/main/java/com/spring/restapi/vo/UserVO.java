package com.spring.restapi.vo;

public class UserVO {
	private String user_id,user_pw,user_name,user_email,user_salt,user_accesstoken,user_refreshtoken;
	private int question_id;
	private String question_answer;
	
	public UserVO() {
		
	}
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_pw() {
		return user_pw;
	}
	public void setUser_pw(String user_pw) {
		this.user_pw = user_pw;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getUser_salt() {
		return user_salt;
	}
	public void setUser_salt(String user_salt) {
		this.user_salt = user_salt;
	}
	public String getUser_accesstoken() {
		return user_accesstoken;
	}
	public void setUser_accesstoken(String user_accesstoken) {
		this.user_accesstoken = user_accesstoken;
	}
	public String getUser_refreshtoken() {
		return user_refreshtoken;
	}
	public void setUser_refreshtoken(String user_refreshtoken) {
		this.user_refreshtoken = user_refreshtoken;
	}
	public int getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(int question_id) {
		this.question_id = question_id;
	}
	public String getQuestion_answer() {
		return question_answer;
	}
	public void setQuestion_answer(String question_answer) {
		this.question_answer = question_answer;
	}

}
