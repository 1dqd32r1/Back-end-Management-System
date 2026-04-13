package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 虚拟团队成员关联实体类
 */
@Data
@TableName("sys_virtual_team_member")
public class SysVirtualTeamMember {

    /** 团队ID */
    private Long teamId;

    /** 用户ID */
    private Long userId;

    /** 成员角色(LEADER/MEMBER/GUEST) */
    private String memberRole;

    /** 加入时间 */
    private LocalDateTime joinTime;
}
