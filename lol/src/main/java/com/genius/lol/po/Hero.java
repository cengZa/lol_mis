package com.genius.lol.po;

import com.baomidou.mybatisplus.annotation.*;
import com.genius.lol.anno.BattleRoute;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Hero {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotEmpty(message = "英雄名称不为空")
    private String heroName;
    @NotEmpty(message = "英雄称号不能为空")
    private String heroTitle;
    @NotEmpty(message = "英雄类型不能为空")
    private HeroType heroType;

    @BattleRoute(message = "无效的战斗路线")
    private String battleRoute;
    private String avatar;

    private String backgroundStory;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum HeroType implements com.baomidou.mybatisplus.annotation.IEnum<String>{

        CONTROLLER("controller"),   // 辅助
        FIGHTER("fighter"), // 战士
        MAGE("mage"),   // 法师
        MARKSMAN("marksman"),   // 射手
        SLAYER("slayer"),   // 刺客
        TANK("tank");   // 坦克

        @EnumValue
        private final String value;

        HeroType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

}
