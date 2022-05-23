package com.spring.restapi.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.restapi.dao.TokenDAO;
import com.spring.restapi.encrypt.RSA2048;
import com.spring.restapi.encrypt.SHA;
import com.spring.restapi.exception.user.InvalidIdException;
import com.spring.restapi.exception.user.InvalidPwException;
import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;
import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.ExpiredJwtException;

@Service("tokenService")
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
		Exception.class
		}
)
public class TokenService {
	@Autowired
	private TokenDAO tokenDAO;
	
	public void refreshTokens(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HashMap result = new HashMap();
		
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);

		//액세스 리프레시 토큰 재발급
		String user_id = JwtUtil.getData(user_accesstoken, "user_id");
		UserVO user = new UserVO();
		user.setUser_id(user_id);

		String new_user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String new_user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//새로운 액세스 리프레시 토큰으로 DB 업데이트
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", new_user_accesstoken);
		param.put("user_refreshtoken", new_user_refreshtoken);
		tokenDAO.updateTokens(param);
		
		//새로운 액세스 리프레시 토큰을 쿠키에 저장후 응답
		response.addCookie(CookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/tokens",14*24*60*60));
		
		//기존 액세스, 리프레시 토큰 비활성화
		RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));
		RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));
	}
	
	public void login(HashMap<String,String> param, HttpServletResponse response) throws InvalidIdException, InvalidPwException, Exception{

		//해당 ID가 있는지 확인
		String user_id = tokenDAO.checkId(param);
		
		//솔트값 얻음
		String user_salt = tokenDAO.getSalt(param);
		
		//비밀키를 얻음
		String privatekey = (String) RedisUtil.getData(param.get("publickey"));
		
		//비밀키로 해싱
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//비밀번호 유효성 검사
		user_pw = tokenDAO.checkPw(param);
		
		//액세스, 리프레쉬 토큰 발급
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		
		String user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//액세스, 리프레쉬 토큰 DB에 저장
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		tokenDAO.updateTokens(param);
		
		//액세스, 리프레쉬 토큰 쿠키에 첨부
		response.addCookie(CookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/tokens",14*24*60*60));
	}
	
	public void logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);
		
		try {
			RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));
		}catch(ExpiredJwtException e) {
			
		}
		
		try {
			RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));
		}catch(ExpiredJwtException e) {

		}
		
		HashMap param = new HashMap();
		param.put("user_id", JwtUtil.getData(user_accesstoken,"user_id"));
		tokenDAO.deleteTokens(param);
		
		//액세스, 리프레쉬 토큰 쿠키에 첨부
		response.addCookie(CookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken","removed","/restapi/tokens",0));
	}
	
	public HashMap getTokens(HashMap param){
		return tokenDAO.getTokens(param);
	}
}
