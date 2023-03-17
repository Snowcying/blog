package com.cxy.service.impl;

import com.cxy.entity.Blog;
import com.cxy.mapper.BlogMapper;
import com.cxy.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cxy
 * @since 2023-03-16
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
