package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "sys_role")
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 30)
    private String roleName;

    @Column(name = "role_key", nullable = false, length = 100)
    private String roleKey;

    @Column(name = "role_sort")
    private Integer roleSort;

    @Column(name = "data_scope", length = 1, columnDefinition = "char(1) default '1'")
    private String dataScope;

    @Column(name = "menu_check_strictly", columnDefinition = "tinyint(1) default 1")
    private Byte menuCheckStrictly;

    @Column(name = "dept_check_strictly", columnDefinition = "tinyint(1) default 1")
    private Byte deptCheckStrictly;

    @Column(name = "status", length = 1, columnDefinition = "char(1) default '0'")
    private String status;

    @Column(name = "del_flag", length = 1, columnDefinition = "char(1) default '0'")
    private String delFlag;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<SysMenu> menus;

    @ManyToMany(mappedBy = "roles")
    private List<SysUser> users;
}