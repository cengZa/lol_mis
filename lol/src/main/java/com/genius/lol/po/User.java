package com.genius.lol.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("user")
public class User {
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    @NotNull
    private String username;

    private String nickname;

    @JsonIgnore
    @NotEmpty
    private String password;

    @Email
    private String email;

    private String avatar;

    @TableField(value = "role")
    private Role role = Role.USER;

    private int status = 1;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum Role implements com.baomidou.mybatisplus.annotation.IEnum<String> {
        ADMIN("admin"),
        USER("user");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }
}
