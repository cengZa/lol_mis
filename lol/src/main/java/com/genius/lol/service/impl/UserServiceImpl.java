package com.genius.lol.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genius.lol.mapper.UserMapper;
import com.genius.lol.po.User;
import com.genius.lol.service.UserService;
import com.genius.lol.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public void register(User user) {
        User existUser = findByUserName(user.getUsername());
        if(existUser == null) {
            String hashed = BCrypt.hashpw(user.getPassword());
            user.setPassword(hashed);
            save(user);
        } else{
            throw new RuntimeException("该用户已存在");
        }
    }

    @Override
    public String login(String username, String rawPassword) {
        User loginingUser = findByUserName(username);
        if(loginingUser == null){
            throw new RuntimeException("该用户不存在");
        }

        if(!BCrypt.checkpw(rawPassword, loginingUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return JwtUtil.generateToken(loginingUser);
    }

    @Override
    public User findByUserName(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username).one();
    }

    @Override
    public User findByUid(Long uid) {
        return lambdaQuery()
                .eq(User::getUid, uid).one();
    }

}

