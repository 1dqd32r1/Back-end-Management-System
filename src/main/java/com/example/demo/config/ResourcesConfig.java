package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件目录
        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:" + getUploadAbsolutePath() + "/");
    }
}
