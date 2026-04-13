package com.example.demo.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.permission.entity.SysVirtualTeamMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 虚拟团队成员关联Mapper
 */
@Mapper
public interface SysVirtualTeamMemberMapper extends BaseMapper<SysVirtualTeamMember> {
}
