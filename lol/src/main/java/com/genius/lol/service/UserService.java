package com.genius.lol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.genius.lol.po.User;

public interface UserService extends IService<User> {

    // 注册
    void register(User user);
    // 登录 返回 token
    String login(String username, String password);

    User findByUserName(String username);
    User findByUid(Long uid);
}
