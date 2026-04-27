package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "用户实体")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Column(length = 100)
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Column(length = 20)
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Column(length = 20)
    @Schema(description = "状态：0正常，1停用", example = "0")
    private String status;

    @Column(name = "create_time", updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Column(length = 50)
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Column(length = 255)
    @Schema(description = "头像URL", example = "/avatar/default.png")
    private String avatar;

    @Column(length = 20)
    @Schema(description = "角色", example = "admin")
    private String role;

    @Column(name = "last_login_time")
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Column(name = "password", length = 255)
    @Schema(description = "密码（加密后存储）", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Column(name = "del_flag", length = 1, columnDefinition = "char(1) default '0'")
    @Schema(description = "删除标志：0未删除，1已删除", example = "0")
    private String delFlag;

    @Column(name = "login_ip", length = 50)
    @Schema(description = "最后登录IP", example = "192.168.1.1")
    private String loginIp;

    @Column(name = "gender", length = 1)
    @Schema(description = "性别：0男，1女，2未知", example = "0")
    private String gender;

    @Column(name = "remark", length = 500)
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = "0"; // 默认状态：正常
        }
        if (delFlag == null) {
            delFlag = "0"; // 默认未删除
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
