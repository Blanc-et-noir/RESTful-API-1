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
	
	//����� ������ �������� ��ū�� ������.
	public static String createToken(UserVO user, long age) {
		
		//1. JWT ��ū�� ��� ������ ������.
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		
		//2. ����� ������ �ش� ��ū�� �߰���. ��, �ΰ��� ������ ��ū�� �߰����� �ʵ��� ��
		Map<String,Object> claims = new HashMap<String,Object>();
		claims.put("user_id", user.getUser_id());
		
		//3. ��ū�� ��ȿ�ð��� ������
		Date now = new Date();
		Date exp = new Date(now.getTime()+age);

		//4. ������ ���, Ŭ���� ������ �������� ���ο� JWT ��ū�� ������.
		return Jwts.builder()
				.setHeader(headers)
				.setClaims(claims)
				.setSubject("user-auth")
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	//Ŭ���̾�Ʈ�κ��� ���޹��� �ش� ��ū�� �����Ǿ����� �ƴ��� �Ǵ���.
	public static boolean validateToken(String token){
		//1. �ش� JWT ��ū�� ������ �ִ��� �Ǵ���.
		try {
			Claims claims = null;
			claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
			return true;
		//2. ��Ÿ ���ܰ� �߻��� ���.
		}catch(Exception e) {
			return false;
		}
	}
	
	//Ŭ���̾�Ʈ�κ��� ���޹��� �ش� ��ū�� ����� ������ ����.
    public static Map<String,Object> getInfo(String token) throws Exception{
    	//1. �ش� JWT ��ū���κ��� ����� ������ ����.
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return claims.getBody();
    	//2. ��Ÿ ���ܰ� �߻��� ���.
    	}catch(Exception e) {
    		return null;
    	}   	
    }
    
    //Ŭ���̾�Ʈ�κ��� ���޹��� �ش� ��ū�� ����� ������ ����.
    public static String getData(String token, String data) throws Exception{
    	//1. �ش� JWT ��ū���κ��� ����� ������ ����.
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return (String) claims.getBody().get(data);
    	//2. �ش� JWT ��ū�� ��ȿ�Ⱓ�� ����� ����� ������ ����.
    	}catch(ExpiredJwtException e) {
    		return (String) e.getClaims().get(data);
    	//3. ��Ÿ ���ܰ� �߻��� ���.
    	}catch(Exception e) {
    		return null;
    	}
    }
    
    //Ŭ���̾�Ʈ�κ��� ���޹��� �ش� ��ū�� ���� ��ȿ�ð��� ����.
    public static Long getExpiration(String token) {
    	//1. �ش� ��ū�� ���� ��ȿ�ð��� ����.
    	try {
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            Long now = new Date().getTime();
            return (expiration.getTime() - now);
        //2. ��Ÿ ���ܰ� �߻��� ���.
    	}catch(Exception e) {
    		return 1L;
    	}
    }
}