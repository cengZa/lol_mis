package com.genius.lol.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.BCrypt;
import com.genius.lol.dto.ChangePwdDTO;
import com.genius.lol.dto.UpdateProfileDTO;
import com.genius.lol.dto.UserFormDTO;
import com.genius.lol.po.User;
import com.genius.lol.service.UserService;
import com.genius.lol.util.AliOssUtil;
import com.genius.lol.util.Result;
import com.genius.lol.util.ThreadLocalUtl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * TODO: 是否需要其他DTO？ 是否需要用户个人介绍/简介？ 是否需要单独的updatePassword？
 */

@Tag(name = "用户管理", description = "用户管理接口实现CURD")
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "用户注册", description = "新增用户接口")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserFormDTO userDTO) {
        User registingUser = new User();
        BeanUtil.copyProperties(userDTO, registingUser);
        userService.register(registingUser);
        log.info("注册用户：{}", registingUser);
        return Result.success("注册成功");
    }

    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String rawPassword) {
        log.info("username={}  rawPassword={}", username, rawPassword);
        String token = userService.login(username, rawPassword);

        // 将token存储到redis中
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, 1, TimeUnit.DAYS);
        log.info("token!!!!  " + "{}", token);

        return Result.success("登录成功", token);
    }

    @PatchMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody @Valid ChangePwdDTO changePwdDTO) {

        String oldPwd = changePwdDTO.getOldPwd();
        String newPwd = changePwdDTO.getNewPwd();
        String confirmPwd = changePwdDTO.getConfirmPwd();

        if(!StringUtils.hasLength(newPwd) || !StringUtils.hasLength(confirmPwd) || !StringUtils.hasLength(oldPwd)) {
            return Result.error(400, "缺少必要的参数");
        }

        String username = ThreadLocalUtl.get("username", String.class);
        if (username == null) {
            return Result.error(403, "用户未登录或登录已过期");
        }

        User loginUser = userService.findByUserName(username);
        if (loginUser == null) {
            return Result.error(404, "用户不存在");
        }

        if(!BCrypt.checkpw(oldPwd, loginUser.getPassword())) {
            return Result.error(401, "原密码错误");
        }

        if(!confirmPwd.equals(newPwd)){
            return Result.error(400, "两次填写的新密码不一致");
        }

        if (BCrypt.checkpw(newPwd, loginUser.getPassword())) {
            return Result.error(400, "新密码不能与旧密码相同");
        }

        loginUser.setPassword(BCrypt.hashpw(newPwd));
        userService.updateById(loginUser);

        // 删除redis对应的token
        String token = ThreadLocalUtl.get("token", String.class);
        if(token != null){
            token = token.replace("Bearer ", ""); // 兼容带 Bearer 的 Token
            stringRedisTemplate.delete(token);
        }

        return Result.success("密码修改成功, 请重新登陆");
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        log.warn("GET: UserInfo ");
        String username = ThreadLocalUtl.get("username", String.class);
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PatchMapping("/update")
    public Result<User> updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO) {
        Long uid = ThreadLocalUtl.getUid();
        User updatingUser = userService.findByUid(uid);
        BeanUtil.copyProperties(updateProfileDTO, updatingUser, CopyOptions.create().ignoreNullValue());
        userService.updateById(updatingUser);
        log.info("更新后的User： {}", updatingUser);
        return Result.success("更新成功", updatingUser);
    }

    /**
     * 允许用户先上传再更新数据库
     */
    @Operation(summary = "上传用户头像")
    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(@RequestPart("avatar") MultipartFile file) {
        // 在avatar目录下
        String fileName = "avatar/" + ThreadLocalUtl.getUid().toString() + "_avatar" + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        try(InputStream in = file.getInputStream()){
            String url = AliOssUtil.uploadFile(fileName, in);
            if(url == null || url.isEmpty()) return Result.error(500, "用户头像上传失败");
            return Result.success("用户头像上传成功", url);
        }catch (IOException e){
            log.error(e.getMessage());
            return Result.error(500, "文件读取失败");
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.error(500, "OSS上传失败");
        }
    }

    @Operation(summary = "更新用户头像")
    @PatchMapping("updateAvatar")
    public Result<Void> updateAvatar(@RequestParam String avatarUrl) {
        Long uid = ThreadLocalUtl.getUid();
        userService.lambdaUpdate().set(User::getAvatar, avatarUrl).eq(User::getUid, uid).update();
        return Result.success("更新头像成功");
    }
}
