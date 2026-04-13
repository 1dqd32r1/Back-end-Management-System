package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    public Result<Page<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      SysUser user) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(user.getUserName())) {
            queryWrapper.like("user_name", user.getUserName());
        }
        if (StringUtils.hasText(user.getPhonenumber())) {
            queryWrapper.like("phonenumber", user.getPhonenumber());
        }
        
        queryWrapper.orderByDesc("create_time");
        return Result.success(userService.page(page, queryWrapper));
    }

    /**
     * 获取详细信息
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getInfo(@PathVariable Long userId) {
        return Result.success(userService.getById(userId));
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<?> add(@RequestBody SysUser user) {
        user.setCreateTime(LocalDateTime.now());
        // 默认密码处理，此处仅为示例
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        userService.save(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<?> edit(@RequestBody SysUser user) {
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public Result<?> remove(@PathVariable String userIds) {
        List<Long> ids = Arrays.stream(userIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        userService.removeByIds(ids);
        return Result.success();
    }
}