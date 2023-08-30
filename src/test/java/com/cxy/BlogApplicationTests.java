package com.cxy;

import com.cxy.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    JwtUtils jwtUtils;

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

}
