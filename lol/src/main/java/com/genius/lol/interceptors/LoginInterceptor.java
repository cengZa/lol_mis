package com.genius.lol.interceptors;

import cn.hutool.jwt.JWT;
import com.genius.lol.util.JwtUtil;
import com.genius.lol.util.ThreadLocalUtl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        // 如果是预检请求，则直接放行
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            // 无token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 兼容 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 3. 检查 Redis 是否存该 Token
            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(token))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 4. 校验 Token
            JWT jwt = JwtUtil.parseAndVerifyToken(token);
            if (jwt == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 5. 解析 JWT 并存入 ThreadLocal
            Map<String, Object> claims = jwt.getPayloads();
            log.info("解析 JWT claims: {}", claims);

            ThreadLocalUtl.put(claims);
            ThreadLocalUtl.put("token", token); // 便于 Controller 读取

            return true;
        } catch (Exception e) {
            log.error("Token 校验失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清空ThreadLocal中的数据
        ThreadLocalUtl.clear();
    }
}
