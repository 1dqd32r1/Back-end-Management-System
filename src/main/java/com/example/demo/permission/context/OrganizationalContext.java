package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 组织维度上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationalContext {

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 部门路径（祖级列表） */
    private String deptPath;

    /** 岗位ID列表 */
    private List<Long> postIds;

    /** 岗位名称列表 */
    private List<String> postNames;

    /** 虚拟团队ID列表 */
    private List<Long> virtualTeamIds;

    /** 汇报线（上级用户ID列表） */
    private List<Long> reportLine;

    /** 是否部门负责人 */
    private Boolean isDeptLeader;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限） */
    private String dataScope;
}
