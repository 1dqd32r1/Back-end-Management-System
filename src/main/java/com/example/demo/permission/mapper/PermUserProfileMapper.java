package com.example.demo.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.permission.entity.PermUserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为画像Mapper
 */
@Mapper
public interface PermUserProfileMapper extends BaseMapper<PermUserProfile> {
}
