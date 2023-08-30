package com.cxy.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.cxy.common.lang.Result;
import com.cxy.entity.User;
import com.cxy.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cxy
 * @since 2023-03-16
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
//    @GetMapping("/{id}")
//    public Object test(@PathVariable("id") Long id) {
//        return userService.getById(id);
//    }

    @RequiresAuthentication
    @GetMapping("/{id}")
    public Result index(@PathVariable("id") Long id) {
//        return
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.fail("没有此用户");
        }
    }

    @RequiresAuthentication
    @GetMapping("/all")
    public Result getAllUser() {
        List<User> users = userService.list(new QueryWrapper<>());
        if (users != null) {
            return Result.success(200, "查询成功", users);
        } else {
            return Result.fail("查询失败");
        }
    }

    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user) {
        return Result.success(user);
    }

}
