package com.example.demo.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.permission.entity.PermDynamicRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 动态权限规则Mapper
 */
@Mapper
public interface PermDynamicRuleMapper extends BaseMapper<PermDynamicRule> {

    /**
     * 查询启用的规则，按优先级排序
     */
    default List<PermDynamicRule> selectEnabledRulesByPriority() {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PermDynamicRule>()
                .eq("status", 1)
                .orderByAsc("priority"));
    }
}
