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
		Exception.class,
		InvalidIdException.class,
		InvalidPwException.class,
		}
)
public class TokenService {
	@Autowired
	private TokenDAO tokenDAO;
	
	//해당 사용자의 액세스, 리프레쉬 토큰을 새로 발급하고, 기존 액세스, 리프레쉬 토큰은 로그아웃 처리함.
	public void refreshTokens(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//1. 해당 사용자의 액세스, 리프레쉬 토큰을 얻음.
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);

		//2. 해당 사용자의 ID를 활용해 새로운 액세스, 리프레쉬 토큰을 발급함.
		String user_id = JwtUtil.getData(user_accesstoken, "user_id");
		UserVO user = new UserVO();
		user.setUser_id(user_id);

		String new_user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String new_user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//3. 해당 사용자의 DB정보를 새로운 액세스, 리프레쉬 토큰으로 갱신함. 
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", new_user_accesstoken);
		param.put("user_refreshtoken", new_user_refreshtoken);
		
		tokenDAO.updateTokens(param);
		
		//4. 새로 발급한 액세스, 리프레쉬 토큰을 쿠키에 담아 클라이언트로 전달함.
		response.addCookie(CookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/tokens",14*24*60*60));
		
		//5. 기존에 발급받았던 액세스, 리프레쉬 토큰은 Redis에 저장하여 로그아웃 처리함.
		RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));
		RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));
	}
	
	//해당 사용자에게 액세스, 리프레쉬 토큰을 새로 발급함.
	public void createTokens(HashMap<String,String> param, HttpServletResponse response) throws InvalidIdException, InvalidPwException, Exception{

		//1. 해당 ID로 가입된 사용자 정보가 존재하는지 확인함.
		String user_id = tokenDAO.checkId(param);
		
		//2. 해당 사용자의 고유 솔트값을 얻음.
		String user_salt = tokenDAO.getSalt(param);
		
		//3. 해당 사용자가 전달한 공개키에 대한 비밀키를 얻음.
		String privatekey = (String) RedisUtil.getData(param.get("publickey"));
		
		//4. 사용자가 전달한 RSA2048로 암호화된 비밀번호를 비밀키로 복호화하고, 솔트값으로 SHA512를 두 번 적용하여 해싱함.
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//5. DB에 솔트값과 함께 두 번 해싱되어 저장된 사용자의 비밀번호와 입력받은 비밀번호를 솔트값과 함께 두 번 해싱한 사용자 비밀번호가 일치하는지 확인함.
		user_pw = tokenDAO.checkPw(param);
		
		//6. 비밀번호가 일치하다면, 새로 액세스, 리프레쉬 토큰을 발급함.
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		String user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//7. 사용자가 발급받은 액세스, 리프레쉬 토큰을 DB에 저장함. 추후에 액세스, 리프레쉬 토큰이 정말로 해당 회원이 발급받은 것인지 확인하기 위함.
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		tokenDAO.updateTokens(param);
		
		//8. 새로 발급한 액세스, 리프레쉬 토큰을 쿠키에 담아 클라이언트에게 전달함.
		response.addCookie(CookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/tokens",14*24*60*60));
	}
	
	//사용자의 토큰 삭제 요청을 처리하는 메소드, 이미 기한이 지난 액세스, 리프레쉬 토큰은 굳이 따로 로그아웃 처리하지 않음.
	public void deleteTokens(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		//1. 해당 사용자의 액세스, 리프레쉬 토큰을 얻음.
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);
		
		//2. 해당 액세스, 리프레쉬 토큰의 남은 시간만큼 Redis의 블랙리스트에 저장하여 로그아웃 처리함.
		try {RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));}catch(ExpiredJwtException e) {}
		try {RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));}catch(ExpiredJwtException e) {}
		
		//3. 해당 액세스 토큰을 발급받았던 사용자의 ID를 얻고, 해당 사용자가 사용중인 토큰 정보를 DB에서 업데이트함.
		HashMap param = new HashMap();
		param.put("user_id", JwtUtil.getData(user_accesstoken,"user_id"));
		tokenDAO.deleteTokens(param);
		
		//4. 클라이언트의 액세스, 리프레쉬 토큰이 담긴 쿠키를 삭제함.
		response.addCookie(CookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken","removed","/restapi/tokens",0));
	}
	
	//해당 사용자가 현재 사용중인 토큰정보를 얻음.
	public HashMap getTokens(HashMap param){
		return tokenDAO.getTokens(param);
	}
}