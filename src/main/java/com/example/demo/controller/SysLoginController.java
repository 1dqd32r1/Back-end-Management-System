package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.model.LoginBody;
import com.example.demo.security.JwtUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SysLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private Producer captchaProducer;

    // 简单本地缓存替代 Redis 用于存储验证码 (生产环境请使用 Redis)
    private static final Map<String, String> CAPTCHA_CACHE = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginBody loginBody) {
        String verifyKey = loginBody.getUuid();
        String captcha = CAPTCHA_CACHE.get(verifyKey);
        CAPTCHA_CACHE.remove(verifyKey);

        if (!StringUtils.hasText(loginBody.getCode()) || !loginBody.getCode().equalsIgnoreCase(captcha)) {
            return Result.error(500, "验证码错误或已失效");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword())
        );

        String token = jwtUtils.generateToken(authentication.getName());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return Result.success(map);
    }

    @GetMapping("/captchaImage")
    public Result<?> getCode() throws IOException {
        String uuid = UUID.randomUUID().toString();
        String capText = captchaProducer.createText();
        CAPTCHA_CACHE.put(uuid, capText);

        BufferedImage image = captchaProducer.createImage(capText);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        
        Map<String, String> ajax = new HashMap<>();
        ajax.put("uuid", uuid);
        ajax.put("img", Base64.getEncoder().encodeToString(os.toByteArray()));
        return Result.success(ajax);
    }
}