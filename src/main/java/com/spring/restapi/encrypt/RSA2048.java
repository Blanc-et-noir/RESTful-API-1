package com.spring.restapi.encrypt;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA2048{
	
	
	
	
	
	//============================================================================================
	//키 객체를 문자열로 변경하는 메소드.
	//============================================================================================
	public static String keyToString(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	
	
	
	
	//============================================================================================
	//특정 문자열을 공개키로 암호화 하는 메소드.
	//============================================================================================
	public static String encrypt(String plaintext, String publickey){
		try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publickeyBytes = Base64.getDecoder().decode(publickey.getBytes());
            
            X509EncodedKeySpec publickeySpec = new X509EncodedKeySpec(publickeyBytes);
            Key key = keyFactory.generatePublic(publickeySpec);
		    byte[] plaintextBytes = plaintext.getBytes();
		    
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.ENCRYPT_MODE,key);
		    byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);
		    return encode(ciphertextBytes);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	//============================================================================================
	//특정 문자열을 비밀키로 복호화 하는 메소드.
	//============================================================================================
	public static String decrypt(String ciphertext, String privatekey){
	    try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] privatekeyBytes = Base64.getDecoder().decode(privatekey.getBytes());
            
            PKCS8EncodedKeySpec privatekeySpec = new PKCS8EncodedKeySpec(privatekeyBytes);
            Key key = keyFactory.generatePrivate(privatekeySpec);
            byte[] ciphertextBytes = decode(ciphertext);
		    
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.DECRYPT_MODE,key);
		    byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
		    return new String(plaintextBytes,"UTF8");
	    }catch(Exception e) {
			e.printStackTrace();
			return null;
	    }
	}
	
	
	
	
	
	//============================================================================================
	//바이트 배열을 문자열로 인코딩하는 메소드.
	//============================================================================================
	private static String encode(byte[] data){
	    return Base64.getEncoder().encodeToString(data);
	}
	
	
	
	
	
	//============================================================================================
	//문자열을 바이트 배열로 디코딩하는 메소드.
	//============================================================================================
	private static byte[] decode(String data){
	    return Base64.getDecoder().decode(data);
	}
	
	
	
	
	
	//============================================================================================
	//RSA2048에서 사용할 공개키, 비밀키쌍을 생성하는 메소드.
	//============================================================================================
	public static KeyPair createKey() {
		KeyPairGenerator gen;
		KeyPair keypair = null;
		try {
			gen = KeyPairGenerator.getInstance("RSA");
			gen.initialize(2048);
			keypair = gen.genKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keypair;
	}
}