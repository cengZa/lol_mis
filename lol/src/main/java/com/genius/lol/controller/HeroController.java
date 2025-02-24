package com.genius.lol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genius.lol.po.Hero;
import com.genius.lol.service.HeroService;
import com.genius.lol.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "英雄管理")
@RequestMapping("/hero")
@RestController
@RequiredArgsConstructor
public class HeroController {

    private static final Logger log = LoggerFactory.getLogger(HeroController.class);
    private final HeroService heroService;

    @Operation(summary = "新增英雄", description = "创建一个新的英雄记录")
    @PostMapping("/add")
    public Result<Hero> addHero(@RequestBody @Validated Hero hero) {
        heroService.save(hero);
        return Result.success(hero);
    }

    @Operation(summary = "获取所有英雄", description = "查询所有英雄数据")
    @GetMapping("/list")
    public Result<Page<Hero>> listHeroes(
            @Parameter(description = "页码(1), 页大小(10)")
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        //* 常见写法是 Controller 收到请求参数后，在Service 或 Mapper 中构造 LambdaQueryWrapper。
        //* 把业务逻辑集中在Service里，更利于维护。
        Page<Hero> temp = heroService.findHeroes(pageNo, pageSize);
        log.info("listHeroes {}", temp.getRecords());
        return Result.success(heroService.findHeroes(pageNo, pageSize));
    }

    @Operation(summary = "获取英雄详情", description = "根据英雄ID获取英雄详情")
    @GetMapping("/detail/{heroId}")
    public Result<Hero> getHeroById(@PathVariable("heroId") Long heroId) {
        Hero hero = heroService.getById(heroId);
        log.info("hero in detail {}", hero);
        if (hero != null) {
            return Result.success(hero);
        } else {
            return Result.error(404, "英雄未找到");
        }
    }

    @Operation(summary = "高级搜索英雄", description = "根据英雄名称、称号、类型、分路进行高级搜索")
    @GetMapping("/advancedSearch")
    public Result<Page<Hero>> advancedSearch(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String heroName,
            @RequestParam(required = false) String heroTitle,
            @RequestParam(required = false) String heroType,
            @RequestParam(required = false) String battleRoute
    ) {
        LambdaQueryWrapper<Hero> wrapper = new LambdaQueryWrapper<>();
        // 英雄名称模糊查询
        if (heroName != null && !heroName.isEmpty()) {
            wrapper.like(Hero::getHeroName, heroName);
        }
        // 英雄称号模糊查询
        if (heroTitle != null && !heroTitle.isEmpty()) {
            wrapper.like(Hero::getHeroTitle, heroTitle);
        }
        // 英雄类型精确匹配
        if (heroType != null && !heroType.isEmpty()) {
            wrapper.eq(Hero::getHeroType, heroType);
        }
        // 英雄分路精确匹配
        if (battleRoute != null && !battleRoute.isEmpty()) {
            wrapper.eq(Hero::getBattleRoute, battleRoute);
        }
        return Result.success(heroService.page(new Page<>(pageNo, pageSize), wrapper));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteHero(@PathVariable("id") Long id) {
        heroService.removeById(id);
        return Result.success("删除成功");
    }


}
