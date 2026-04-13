package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // 添加 avatar 字段
            jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN avatar varchar(255) DEFAULT '' COMMENT '头像地址'");
        } catch (Exception e) {
            // 字段已存在，忽略
        }

        // 确保用户数据存在
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_user WHERE user_name = 'admin'", Integer.class);
        if (count == null || count == 0) {
            jdbcTemplate.update(
                "INSERT INTO sys_user (user_name, nick_name, password, email, phonenumber, sex, avatar, status, create_time) " +
                "VALUES ('admin', '管理员', '123456', 'admin@example.com', '15888888888', '0', '', '0', NOW())");
            System.out.println(">>> 初始化用户数据: admin / 123456");
        } else {
            // 确保密码是明文 123456
            jdbcTemplate.update("UPDATE sys_user SET password = '123456' WHERE user_name = 'admin'");
            System.out.println(">>> 用户密码已更新: admin / 123456");
        }
    }
}
