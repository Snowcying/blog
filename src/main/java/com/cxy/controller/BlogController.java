package com.cxy.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cxy.common.lang.Result;
import com.cxy.entity.Blog;
import com.cxy.service.BlogService;
import com.cxy.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cxy
 * @since 2023-03-16
 */
@Slf4j
@RestController
//@RequestMapping("/blog")
public class BlogController {

    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));

        return Result.success(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result getOne(@PathVariable(name="id") Long id) {

        Blog blog = blogService.getById(id);
        Assert.notNull(blog,"该博客已被删除");
        return Result.success(blog);
    }

    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result editOrAdd(@Validated @RequestBody Blog blog) {
        Blog temp;
        if(blog.getId() != null) {
            log.info("editOrAdd");
            temp = blogService.getById(blog.getId());
            // 只能编辑自己的文章
            System.out.println(ShiroUtil.getProfile().getId());
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限编辑");

        } else {

            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtil.copyProperties(blog, temp, "id", "userId", "created", "status");
        blogService.saveOrUpdate(temp);
        return Result.success(null);
    }


    @RequiresAuthentication
    @DeleteMapping("/blog/{id}")
    public Result delete(@PathVariable(name="id") Long id) {
        Blog blog = blogService.getById(id);
        // 只能编辑自己的文章
        Assert.isTrue(blog.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限删除");
        blogService.removeById(id);
        return Result.success(null);
    }

}
