package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "sys_dept")
public class SysDept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column(name = "parent_id", columnDefinition = "bigint(20) default 0")
    private Long parentId;

    @Column(name = "ancestors", length = 500)
    private String ancestors;

    @Column(name = "dept_name", nullable = false, length = 30)
    private String deptName;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "leader", length = 20)
    private String leader;

    @Column(name = "phone", length = 11)
    private String phone;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "status", length = 1, columnDefinition = "char(1) default '0'")
    private String status;

    @Column(name = "del_flag", length = 1, columnDefinition = "char(1) default '0'")
    private String delFlag;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SysDept parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<SysDept> children;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_dept",
            joinColumns = @JoinColumn(name = "dept_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<SysUser> users;
}