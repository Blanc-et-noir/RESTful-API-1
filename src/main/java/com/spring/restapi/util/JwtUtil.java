package com.spring.restapi.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	//�ӽ÷� ���Ű�� �ϵ��ڵ��Ͽ����� ���� ���濹��
	private static String secretKey = Base64.getEncoder().encodeToString("��ĪŰ".getBytes());
	public static final long refreshtokenMaxAge = 14*24*60*60*1000;
	//public static final long accesstokenMaxAge = 1*60*60*1000;
	public static final long accesstokenMaxAge = 10*1000;
	public static final int privateKeyMaxAge = 1*60*1000;
	
	//����� ������ �������� ��ū�� �����ϴ� �޼ҵ�
	public static String createToken(UserVO user, long age) {
		Map<String,Object> headers = new HashMap<String,Object>();
		
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		
		//����� ������ �ش� ��ū�� �߰���
		//��, �ΰ��� ������ ��ū�� �߰����� �ʵ��� ��
		Map<String,Object> claims = new HashMap<String,Object>();
		claims.put("user_id", user.getUser_id());
		
		//��ū�� ��ȿ�ð��� ������
		Date now = new Date();
		Date exp = new Date(now.getTime()+age);

		//��ū�� �߱���
		return Jwts.builder()
				.setHeader(headers)
				.setClaims(claims)
				.setSubject("user-auth")
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	//�ش� ��ū�� ��ȿ���� �ƴ��� �Ǵ��ϴ� �޼ҵ�
	public static boolean validateToken(String token) throws ExpiredJwtException, Exception{
		Claims claims = null;
		//�ش� ��ū�� ��ȿ���� ������
		claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return true;
	}
	
	//�ش� ��ū�� ÷���� ������ ��� �� ��ü�� ����
    public static Map<String,Object> getInfo(String token) throws Exception{
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return claims.getBody();
    	}catch(Exception e) {
    		return null;
    	}   	
    }
    
    //�ش� ��ū�� ��� ������ ����
    public static String getData(String token, String data) throws Exception{
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return (String) claims.getBody().get(data);
    	}catch(ExpiredJwtException e) {
    		return (String) e.getClaims().get(data);
    	}catch(Exception e) {
    		return null;
    	}
    }
    
    //�ش� ��ū�� ���� ��ȿ�ð��� ����
    public static Long getExpiration(String token) {
    	try {
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            Long now = new Date().getTime();
            return (expiration.getTime() - now);
    	}catch(Exception e) {
    		return 1L;
    	}
    }
}