package com.cxy.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cxy.common.dto.LoginDto;
import com.cxy.common.lang.Result;
import com.cxy.entity.User;
import com.cxy.service.UserService;
import com.cxy.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response, Model model) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));

        // 返回 IllegalArgumentException异常，需要去exception添加该异常
//        Assert.notNull(user, "用户不存在");

        if (user == null || !user.getPassword().equals(SecureUtil.md5(password))) {
            return Result.fail(400, "账户或密码有误", null);
        }

//        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
//            return Result.fail("密码不正确");
//        }
        String jwt = jwtUtils.generateToken(user.getId(), new Date());

        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        model.addAttribute("jwt", jwt);

        return Result.success(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success(null);
    }
}
