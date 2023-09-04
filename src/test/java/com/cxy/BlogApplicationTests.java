package com.cxy;

import com.alibaba.fastjson.JSON;
import com.cxy.config.MallESConfig;
import com.cxy.constant.EsConstant;
import com.cxy.entity.Blog;
import com.cxy.entity.User;
import com.cxy.service.BlogService;
import com.cxy.service.SearchBlogService;
import com.cxy.service.UserService;
import com.cxy.util.JwtUtils;
import com.cxy.vo.BlogESVo;
import com.cxy.vo.SearchParam;
import com.cxy.vo.SearchResult;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RestHighLevelClient highLevelClient;

    @Autowired
    BlogService blogService;
    @Autowired
    UserService userService;

    @Autowired
    SearchBlogService searchBlogService;

    @Test
    void contextLoads() {
        System.out.println("hello");
    }

    @Test
    void testJwtValid() {
//        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjkzMTk4OTk3LCJleHAiOjE2OTM4MDM3OTd9.IGk7NeST4RoHiBi1Z9shCj3g1WcXHIUZS9-0HkPxHSx3AZDJMDRDCBjXgnDzrS-rBpi5G0u-whrk3UGWdi0Cmw";
        String inValidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNjkzMTk4OTk3LCJleHAiOjE2OTM4MDM3OTd9.IGk7NeST4RoHiBi1Z9shCj3g1WcXHIUZS9-0HkPxHSx3AZDJMDRDCBjXgnDzrS-rBpi5G0u-whrk3UGWdi0Cmw";

        boolean valid = jwtUtils.getValid(inValidToken);
        System.out.println(valid);
    }

    @Test
    void testES() throws IOException {
        System.out.println(highLevelClient);
        BlogESVo vo = new BlogESVo();
        vo.setId(2L);
        vo.setDescription("des2");
        vo.setTitle("title2");
        vo.setContent("content2byUpdate");
        vo.setUsername("cxyy");
        vo.setUserId(22L);
        List<BlogESVo> vos = new ArrayList<>();
        vos.add(vo);

        BulkRequest bulkRequest = new BulkRequest();
        for (BlogESVo model : vos) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = highLevelClient.bulk(bulkRequest, MallESConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        System.out.println(b);
    }

    @Test
    void testESByMySQL() throws IOException {
//        Long id = 9L;
        List<Blog> list = blogService.list();
        List<BlogESVo> vos = list.stream().map(item -> {
            Long id = item.getId();
            User user = userService.getById(item.getUserId());
            BlogESVo vo = new BlogESVo();
            BeanUtils.copyProperties(item, vo);
            vo.setUsername(user.getUsername());
            return vo;
        }).collect(Collectors.toList());


//        System.out.println(blogESVo);

//        List<BlogESVo> vos = new ArrayList<>();
//        vos.add(vo);

        BulkRequest bulkRequest = new BulkRequest();
        for (BlogESVo model : vos) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = highLevelClient.bulk(bulkRequest, MallESConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        System.out.println(b);

    }

    @Test
    void testQueryES() {
        SearchParam searchParam = new SearchParam();
        searchParam.setContent("事务");
        searchParam.setDescription("shiro");
        SearchResult result = searchBlogService.search(searchParam);

    }


}
