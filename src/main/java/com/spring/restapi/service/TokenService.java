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
	
	//�ش� ������� �׼���, �������� ��ū�� ���� �߱��ϰ�, ���� �׼���, �������� ��ū�� �α׾ƿ� ó����.
	public void refreshTokens(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//1. �ش� ������� �׼���, �������� ��ū�� ����.
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);

		//2. �ش� ������� ID�� Ȱ���� ���ο� �׼���, �������� ��ū�� �߱���.
		String user_id = JwtUtil.getData(user_accesstoken, "user_id");
		UserVO user = new UserVO();
		user.setUser_id(user_id);

		String new_user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String new_user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//3. �ش� ������� DB������ ���ο� �׼���, �������� ��ū���� ������. 
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", new_user_accesstoken);
		param.put("user_refreshtoken", new_user_refreshtoken);
		
		tokenDAO.updateTokens(param);
		
		//4. ���� �߱��� �׼���, �������� ��ū�� ��Ű�� ��� Ŭ���̾�Ʈ�� ������.
		response.addCookie(CookieUtil.createCookie("user_accesstoken", new_user_accesstoken, "/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken", new_user_refreshtoken, "/restapi/tokens",14*24*60*60));
		
		//5. ������ �߱޹޾Ҵ� �׼���, �������� ��ū�� Redis�� �����Ͽ� �α׾ƿ� ó����.
		RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));
		RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));
	}
	
	//�ش� ����ڿ��� �׼���, �������� ��ū�� ���� �߱���.
	public void createTokens(HashMap<String,String> param, HttpServletResponse response) throws InvalidIdException, InvalidPwException, Exception{

		//1. �ش� ID�� ���Ե� ����� ������ �����ϴ��� Ȯ����.
		String user_id = tokenDAO.checkId(param);
		
		//2. �ش� ������� ���� ��Ʈ���� ����.
		String user_salt = tokenDAO.getSalt(param);
		
		//3. �ش� ����ڰ� ������ ����Ű�� ���� ���Ű�� ����.
		String privatekey = (String) RedisUtil.getData(param.get("publickey"));
		
		//4. ����ڰ� ������ RSA2048�� ��ȣȭ�� ��й�ȣ�� ���Ű�� ��ȣȭ�ϰ�, ��Ʈ������ SHA512�� �� �� �����Ͽ� �ؽ���.
		String user_pw = SHA.DSHA512(RSA2048.decrypt(param.get("user_pw"), privatekey),user_salt);
		param.put("user_pw", user_pw);
		
		//5. DB�� ��Ʈ���� �Բ� �� �� �ؽ̵Ǿ� ����� ������� ��й�ȣ�� �Է¹��� ��й�ȣ�� ��Ʈ���� �Բ� �� �� �ؽ��� ����� ��й�ȣ�� ��ġ�ϴ��� Ȯ����.
		user_pw = tokenDAO.checkPw(param);
		
		//6. ��й�ȣ�� ��ġ�ϴٸ�, ���� �׼���, �������� ��ū�� �߱���.
		UserVO user = new UserVO();
		user.setUser_id(user_id);
		
		String user_accesstoken = JwtUtil.createToken(user, JwtUtil.accesstokenMaxAge);
		String user_refreshtoken = JwtUtil.createToken(user, JwtUtil.refreshtokenMaxAge);
		
		//7. ����ڰ� �߱޹��� �׼���, �������� ��ū�� DB�� ������. ���Ŀ� �׼���, �������� ��ū�� ������ �ش� ȸ���� �߱޹��� ������ Ȯ���ϱ� ����.
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		tokenDAO.updateTokens(param);
		
		//8. ���� �߱��� �׼���, �������� ��ū�� ��Ű�� ��� Ŭ���̾�Ʈ���� ������.
		response.addCookie(CookieUtil.createCookie("user_accesstoken",user_accesstoken,"/restapi",14*24*60*60));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken",user_refreshtoken,"/restapi/tokens",14*24*60*60));
	}
	
	//������� ��ū ���� ��û�� ó���ϴ� �޼ҵ�, �̹� ������ ���� �׼���, �������� ��ū�� ���� ���� �α׾ƿ� ó������ ����.
	public void deleteTokens(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		//1. �ش� ������� �׼���, �������� ��ū�� ����.
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String user_refreshtoken = CookieUtil.getRefreshtoken(request);
		
		//2. �ش� �׼���, �������� ��ū�� ���� �ð���ŭ Redis�� ������Ʈ�� �����Ͽ� �α׾ƿ� ó����.
		try {RedisUtil.setData(user_accesstoken, "removed", JwtUtil.getExpiration(user_accesstoken));}catch(ExpiredJwtException e) {}
		try {RedisUtil.setData(user_refreshtoken, "removed", JwtUtil.getExpiration(user_refreshtoken));}catch(ExpiredJwtException e) {}
		
		//3. �ش� �׼��� ��ū�� �߱޹޾Ҵ� ������� ID�� ���, �ش� ����ڰ� ������� ��ū ������ DB���� ������Ʈ��.
		HashMap param = new HashMap();
		param.put("user_id", JwtUtil.getData(user_accesstoken,"user_id"));
		tokenDAO.deleteTokens(param);
		
		//4. Ŭ���̾�Ʈ�� �׼���, �������� ��ū�� ��� ��Ű�� ������.
		response.addCookie(CookieUtil.createCookie("user_accesstoken","removed","/restapi",0));
		response.addCookie(CookieUtil.createCookie("user_refreshtoken","removed","/restapi/tokens",0));
	}
	
	//�ش� ����ڰ� ���� ������� ��ū������ ����.
	public HashMap getTokens(HashMap param){
		return tokenDAO.getTokens(param);
	}
}