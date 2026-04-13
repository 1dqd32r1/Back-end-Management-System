package com.example.demo.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getOne(new QueryWrapper<SysUser>().eq("user_name", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else if ("1".equals(user.getStatus())) {
            throw new RuntimeException("对不起，您的账号已停用");
        }
        return new LoginUser(user);
    }
}