package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "sys_menu")
public class SysMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "parent_id", columnDefinition = "bigint(20) default 0")
    private Long parentId;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "path", length = 200)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "menu_type", length = 1)
    private String menuType;

    @Column(name = "visible", length = 1, columnDefinition = "char(1) default '0'")
    private String visible;

    @Column(name = "is_refresh", columnDefinition = "tinyint(1) default 1")
    private Byte isRefresh;

    @Column(name = "is_cache", columnDefinition = "tinyint(1) default 0")
    private Byte isCache;

    @Column(name = "perms", length = 100)
    private String perms;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SysMenu parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<SysMenu> children;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<SysRole> roles;
}