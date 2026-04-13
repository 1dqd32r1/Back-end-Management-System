package com.example.demo.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.permission.entity.PermChangeEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限变更事件Mapper
 */
@Mapper
public interface PermChangeEventMapper extends BaseMapper<PermChangeEvent> {
}
