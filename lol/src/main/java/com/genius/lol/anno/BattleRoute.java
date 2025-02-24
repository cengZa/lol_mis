package com.genius.lol.anno;

import com.genius.lol.validation.BattleRouteValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) // 元注解 用于字段
@Retention(RetentionPolicy.RUNTIME) // 元注解 保留到RUNTIME
@Documented // 元注解
@Constraint(
        validatedBy = {BattleRouteValidation.class}
)
public @interface BattleRoute {
    // 提供校验失败后的提示信息
    String message() default " 分路不合法 ";
    // 制定分组
    Class<?>[] groups() default {};
    // 负载 获取到该注解的附加信息
    Class<? extends Payload>[] payload() default {};

    String[] acceptedValues() default {"TOP", "JUG", "MID", "ADC", "SUP"};
}
