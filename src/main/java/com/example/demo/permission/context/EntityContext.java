package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体维度上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityContext {

    /** 当前操作实体类型（project/customer/order/asset） */
    private String entityType;

    /** 当前操作实体ID */
    private Long entityId;

    /** 实体名称 */
    private String entityName;

    /** 实体所属项目ID */
    private Long projectId;

    /** 实体所属客户ID */
    private Long customerId;

    /** 实体敏感级别 */
    private Integer sensitivityLevel;

    /** 用户对该实体的权限类型（view/edit/delete/own） */
    private String permissionType;

    /** 是否有访问权限 */
    private Boolean hasAccess;
}
