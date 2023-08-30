package com.cxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class SsoController {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @RequestMapping("/sso/loginSuccess/{token}/**")
    public String sso(@PathVariable("token") String token, Model model, HttpServletRequest request) {
        // 获取完整的路径
        String uri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 获取映射的路径
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 截取带“/”的参数
        String customPath = antPathMatcher.extractPathWithinPattern(pattern, uri);

        long time = new Date().getTime();
        token = token + time;
        model.addAttribute("token", token);
        model.addAttribute("backUrl", customPath);
//        System.out.println(token);
//        System.out.println(backUrl);
        return "/login";
    }

    @RequestMapping("/sso/login/**")
    public String queryToken(Model model, HttpServletRequest request) {
        // 获取完整的路径
        String uri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 获取映射的路径
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 截取带“/”的参数
        String customPath = antPathMatcher.extractPathWithinPattern(pattern, uri);

        model.addAttribute("backUrl", customPath);
        return "/queryToken";
    }


}
