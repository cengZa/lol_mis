package com.genius.lol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genius.lol.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
