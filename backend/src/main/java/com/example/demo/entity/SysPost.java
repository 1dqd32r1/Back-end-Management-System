package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "sys_post")
public class SysPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "post_code", nullable = false, length = 64)
    private String postCode;

    @Column(name = "post_name", nullable = false, length = 50)
    private String postName;

    @Column(name = "post_sort")
    private Integer postSort;

    @Column(name = "status", length = 1, columnDefinition = "char(1) default '0'")
    private String status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<SysUser> users;
}