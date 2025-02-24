package com.genius.lol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePwdDTO {

    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;
    @NotBlank(message = "新密码不能为空")
    @Size(min = 1, message = "新密码至少1位")
    private String newPwd;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
}
