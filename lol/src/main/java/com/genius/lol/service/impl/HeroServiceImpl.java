package com.genius.lol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genius.lol.mapper.HeroMapper;
import com.genius.lol.po.Hero;
import com.genius.lol.service.HeroService;
import org.springframework.stereotype.Service;

@Service
public class HeroServiceImpl extends ServiceImpl<HeroMapper, Hero> implements HeroService {
    public Page<Hero> findHeroes(int pageNo, int pageSize){
        LambdaQueryWrapper<Hero> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Hero::getCreateTime).orderByDesc(Hero::getHeroTitle);
        return this.page(new Page<>(pageNo, pageSize), queryWrapper);
    }
}
