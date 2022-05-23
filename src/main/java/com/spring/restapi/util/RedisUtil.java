package com.spring.restapi.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {

    private static RedisTemplate redisTemplate;

    @Autowired
    RedisUtil(RedisTemplate redisTemplate){
    	this.redisTemplate = redisTemplate;
    }
    
    public static Object getData(String key){
    	try {
            return redisTemplate.opsForValue().get(key);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public static void setData(String key, String value){
        redisTemplate.opsForValue().set(key,value);
    }

    public static void setData(String key,String value,long duration){
		redisTemplate.opsForValue().set(key, value, duration, TimeUnit.MILLISECONDS);
    }

    public static void deleteData(String key){
    	redisTemplate.delete(key);
    }
}