-- 初始化脚本：添加头像字段并确保用户数据正确

USE demo_admin;

-- 添加 avatar 字段（如果不存在）
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS avatar varchar(255) DEFAULT '' COMMENT '头像地址';

-- 确保用户数据存在，密码为明文 123456
INSERT INTO sys_user (user_id, user_name, nick_name, password, email, phonenumber, sex, avatar, status, create_time)
VALUES (1, 'admin', '管理员', '123456', 'admin@example.com', '15888888888', '0', '', '0', NOW())
ON DUPLICATE KEY UPDATE password = '123456';

-- 查看结果
SELECT user_id, user_name, nick_name, password, avatar FROM sys_user;
