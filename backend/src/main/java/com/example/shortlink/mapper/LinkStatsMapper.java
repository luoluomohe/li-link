package com.example.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shortlink.entity.LinkStats;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LinkStatsMapper extends BaseMapper<LinkStats> {
}