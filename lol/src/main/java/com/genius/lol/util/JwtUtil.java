package com.genius.lol.util;

import cn.hutool.jwt.JWT;
import com.genius.lol.po.User;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "secret_key";

    private static final long EXPIRE_TIME = 60 * 60 * 24; // 24 hour
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public static String generateToken(User user){
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", user.getUid());
        payload.put("username", user.getUsername());
        payload.put("role", user.getRole());

        long now = Instant.now().getEpochSecond();
        long exp = now + EXPIRE_TIME;

        // Just for test TODO: Remember to delete
        LocalDateTime n = LocalDateTime.ofInstant(Instant.ofEpochSecond(now), ZoneId.systemDefault());
        LocalDateTime e = LocalDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault());
        log.warn("putting now {} exp {} when login in generateToken()", n, e);
        
        payload.put("exp", exp);
        return JWTUtil.createToken(payload, SECRET.getBytes());
    }

    public static JWT parseAndVerifyToken(String token){
        JWT jwt = JWTUtil.parseToken(token);
        boolean verify = JWTUtil.verify(token, SECRET.getBytes());
        if(!verify) return null;

        Object expObj = jwt.getPayload("exp");
        if(expObj instanceof Number){
            long exp = ((Number) expObj).longValue();
            Instant expInstant = Instant.ofEpochSecond(exp);
            LocalDateTime expLDT = LocalDateTime.ofInstant(expInstant, ZoneId.systemDefault());
            log.info("exp in claims  {}", expLDT);

            long now = Instant.now().getEpochSecond();
            if(now > exp) return null;
        } else return null;

        return jwt;
    }

}
