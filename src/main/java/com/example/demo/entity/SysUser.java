package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    private String userName;
    
    private String nickName;
    
    private String password;
    
    private String email;
    
    private String phonenumber;
    
    private String sex;

    private String avatar;

    private String status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}