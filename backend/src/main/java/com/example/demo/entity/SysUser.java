package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Column(name = "nick_name", length = 30)
    private String nickName;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "phonenumber", length = 11)
    private String phonenumber;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "sex", length = 1)
    private String sex;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "status", length = 1, columnDefinition = "char(1) default '0'")
    private String status;

    @Column(name = "del_flag", length = 1, columnDefinition = "char(1) default '0'")
    private String delFlag;

    @Column(name = "login_ip")
    private String loginIp;

    @Column(name = "login_date")
    private Date loginDate;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<SysRole> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_post",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<SysPost> posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private SysDept dept;
}