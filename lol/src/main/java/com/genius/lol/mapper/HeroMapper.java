package com.genius.lol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genius.lol.po.Hero;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HeroMapper extends BaseMapper<Hero> {
}
