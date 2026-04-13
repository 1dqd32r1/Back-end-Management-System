package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_entity")
public class SysEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entityId;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id_ref", nullable = false)
    private Long entityIdRef;

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_entity",
            joinColumns = @JoinColumn(name = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<SysUser> users;
}