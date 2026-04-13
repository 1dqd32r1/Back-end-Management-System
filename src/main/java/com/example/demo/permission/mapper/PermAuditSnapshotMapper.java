package com.example.demo.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.permission.entity.PermAuditSnapshot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限审计快照Mapper
 */
@Mapper
public interface PermAuditSnapshotMapper extends BaseMapper<PermAuditSnapshot> {
}
