package com.example.demo.factory;

import com.example.demo.entity.User;

import java.time.LocalDateTime;

/**
 * 用户测试数据工厂
 * 提供构建测试数据的工具方法
 */
public class UserTestDataFactory {

    /**
     * 创建默认测试用户
     */
    public static User createDefaultUser() {
        return User.builder()
                .username("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .status("0")
                .nickname("测试用户")
                .gender("0")
                .role("USER")
                .delFlag("0")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建带ID的测试用户
     */
    public static User createUserWithId(Long id) {
        User user = createDefaultUser();
        user.setId(id);
        return user;
    }

    /**
     * 创建待激活状态的测试用户
     */
    public static User createPendingUser() {
        User user = createDefaultUser();
        user.setStatus("1");
        return user;
    }

    /**
     * 创建已禁用状态的测试用户
     */
    public static User createDisabledUser() {
        User user = createDefaultUser();
        user.setStatus("1");
        return user;
    }

    /**
     * 创建自定义用户
     */
    public static User createCustomUser(String username, String email, String phone, String status) {
        return User.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .status(status)
                .delFlag("0")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建最小用户（仅必填字段）
     */
    public static User createMinimalUser() {
        return User.builder()
                .username("minimaluser")
                .status("0")
                .delFlag("0")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建无效用户（用户名为空）
     */
    public static User createInvalidUserWithNullUsername() {
        return User.builder()
                .username(null)
                .email("test@example.com")
                .status("0")
                .delFlag("0")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建邮箱格式无效的用户
     */
    public static User createUserWithInvalidEmail() {
        return User.builder()
                .username("testuser")
                .email("invalid-email")
                .status("0")
                .delFlag("0")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建用于更新的用户详情
     */
    public static User createUserForUpdate() {
        return User.builder()
                .username("updateduser")
                .email("updated@example.com")
                .phone("13900139000")
                .status("0")
                .delFlag("0")
                .build();
    }
}
