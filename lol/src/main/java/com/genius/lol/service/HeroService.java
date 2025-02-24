package com.genius.lol.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.genius.lol.po.Hero;

public interface HeroService extends IService<Hero> {
    Page<Hero> findHeroes(int pageNo, int pageSize);
}
