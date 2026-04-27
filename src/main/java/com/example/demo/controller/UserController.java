package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "支持关键词搜索和状态筛选")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public Result<Map<String, Object>> getUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向：asc升序，desc降序") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "搜索关键词：支持用户名、邮箱、手机号、昵称模糊查询") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态：0正常，1停用") @RequestParam(required = false) String status) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, sortBy) :
                Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = (keyword != null || status != null) ?
                userService.searchUsers(keyword, status, pageable) :
                userService.getAllUsers(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", userPage.getContent());
        response.put("totalElements", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("currentPage", userPage.getNumber());
        response.put("pageSize", userPage.getSize());

        return Result.success(response);
    }

    @GetMapping("/active")
    @Operation(summary = "获取所有活跃用户", description = "获取所有未删除的用户列表")
    public Result<List<User>> getActiveUsers() {
        List<User> users = userService.getAllActiveUsers();
        return Result.success(users);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询活跃用户信息")
    public Result<User> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        return userService.getActiveUserByUsername(username)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户", description = "根据用户ID获取用户详细信息")
    public Result<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户，密码会自动加密存储")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public Result<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return Result.success(createdUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息，不包括密码")
    public Result<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return Result.success(updatedUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "软删除用户，将删除标志设置为1")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询用户", description = "查询指定状态的活跃用户列表")
    public Result<List<User>> getUsersByStatus(
            @Parameter(description = "状态：0正常，1停用") @PathVariable String status) {
        List<User> users = userService.getUsersByStatus(status);
        return Result.success(users);
    }

    @PostMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "密码修改请求",
            content = @Content(schema = @Schema(example = "{\"oldPassword\":\"old123\",\"newPassword\":\"new123\"}"))
    )
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Map<String, String> passwordRequest) {
        try {
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");
            if (oldPassword == null || newPassword == null) {
                return Result.error("请提供旧密码和新密码");
            }
            userService.changePassword(id, oldPassword, newPassword);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Map<String, String> passwordRequest) {
        try {
            String newPassword = passwordRequest.get("newPassword");
            if (newPassword == null) {
                return Result.error("请提供新密码");
            }
            userService.resetPassword(id, newPassword);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/login-info")
    @Operation(summary = "更新登录信息", description = "更新用户的登录IP和登录时间")
    public Result<Void> updateLoginInfo(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Map<String, String> loginInfo) {
        try {
            String loginIp = loginInfo.get("loginIp");
            userService.updateLoginInfo(id, loginIp);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
