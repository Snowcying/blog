package com.cxy.shiro;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.cxy.common.lang.Result;
import com.cxy.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.hutool.http.ContentType.JSON;

@Slf4j
@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;
    private static final ObjectMapper MAPPER = new ObjectMapper();


//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        try {
//            log.info("isAccessAllowed阶段");
////            //System.out.println("isAccessaLLOWED");
////            //得到客户端传过来的令牌
////            String token = ((HttpServletRequest) request).getHeader("token");
////            //System.out.println("客户端令牌"+token);
////            //封装
////            JwtToken jwtToken = new JwtToken(token);
////            //登录:实际是调用LoginRealm里面doGetAuthenticationInfo
////            getSubject(request, response).login(jwtToken);
////            //判断是否有权限
////            String url = ((HttpServletRequest) request).getRequestURI();
////            getSubject(request, response).checkPermission(url);
//
//            return true;
//
//        } catch (Exception e) {
//            return false;
//        }
//    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest request=(HttpServletRequest)servletRequest;
        String jwt=request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
            return null;
        }

        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        String jwt=request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
           return true;
        }else{
            Claims claim = jwtUtils.getClaimByToken(jwt);
            if(claim == null || jwtUtils.isTokenExpired(claim.getExpiration())){
                Result r=Result.fail(401,"token is out",null);
                String json = MAPPER.writeValueAsString(r);
                servletResponse.getWriter().print(json);
//                servletResponse

                throw new ExpiredCredentialsException("token 失效");
            }


            return executeLogin(servletRequest,servletResponse);
        }
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.info("检查登录");
        HttpServerResponse httpServerResponse=(HttpServerResponse)response;

        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.fail(throwable.getMessage());

        String json = JSONUtil.toJsonStr(result);
        try {
            httpServerResponse.getWriter().print(json);
        } catch (Exception ex) {

//            throw new RuntimeException(ex);
        }

        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}
