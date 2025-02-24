package com.genius.lol.validation;

import com.genius.lol.anno.BattleRoute;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  自定义注解的尝试  学习用
 *  最佳实践为 java枚举 + MyBatis-Plus的IENUM 统一使用Jackson反序列化+全局异常处理捕获无效枚举值
 *  相关代码段 com.genius.lol.anno.BattleRoute
 */
public class BattleRouteValidation implements ConstraintValidator<BattleRoute, String> {

    private Set<String> acceptedRoutes;

    @Override
    public void initialize(BattleRoute constraintAnnotation) {
        this.acceptedRoutes = new HashSet<>(Arrays.asList(constraintAnnotation.acceptedValues()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null)   return true;
        if(value.trim().isEmpty())  return false;
        return acceptedRoutes.contains(value);
    }
}
