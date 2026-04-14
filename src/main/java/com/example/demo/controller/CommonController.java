package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "通用接口", description = "通用功能接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

    @Autowired
    private ISysUserService userService;

    /**
     * 获取上传目录的绝对路径
     */
    private String getUploadAbsolutePath() {
        String path = uploadPath;
        // 如果是相对路径，转换为相对于项目根目录的绝对路径
        if (path.startsWith(".")) {
            String userDir = System.getProperty("user.dir");
            path = userDir + File.separator + path.substring(1);
        }
        return path;
    }

    /**
     * 上传头像
     */
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    @PostMapping("/uploadAvatar")
    public Result<?> uploadAvatar(@Parameter(description = "头像文件") @RequestParam("file") MultipartFile file,
                                   @Parameter(description = "用户ID") @RequestParam("userId") Long userId) {
        if (file.isEmpty()) {
            return Result.error(500, "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error(500, "文件名不能为空");
        }

        // 检查文件类型
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!".jpg".equals(suffix) && !".jpeg".equals(suffix) && !".png".equals(suffix) && !".gif".equals(suffix)) {
            return Result.error(500, "文件格式不正确，请上传 jpg/png/gif 格式的图片");
        }

        // 生成新文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String avatarPath = "/avatar/";
        String absoluteUploadPath = getUploadAbsolutePath();
        String fullPath = absoluteUploadPath + avatarPath;

        // 确保目录存在
        File dir = new File(fullPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                return Result.error(500, "创建上传目录失败");
            }
        }

        try {
            Path targetPath = Paths.get(fullPath, newFileName);
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }

        // 更新用户头像
        String avatarUrl = "/profile" + avatarPath + newFileName;
        SysUser user = userService.getById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            user.setUpdateTime(LocalDateTime.now());
            userService.updateById(user);
        }

        Map<String, String> data = new HashMap<>();
        data.put("imgUrl", avatarUrl);
        return Result.success(data);
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/getInfo")
    public Result<?> getInfo(@RequestAttribute("userId") Long userId) {
        SysUser user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }
}
