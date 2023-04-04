package com.cxy.shiro;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cxy.common.lang.Result;
import com.cxy.util.JwtUtils;
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
import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        try {
//            log.info("isAccessAllowed阶段");
////            //System.out.println("isAccessALLOWED");
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
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse)  {
        log.info("createToken阶段");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)) {
            return null;
        }

        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        log.info("onAccessDenied阶段");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)) {
            return true;
        } else {
            Claims claim = jwtUtils.getClaimByToken(jwt);
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
//                Result r=Result.fail(401,"token is out",null);
//                String json = MAPPER.writeValueAsString(r);
//                servletResponse.getWriter().print(json);
                Result.failReturnJson(401, "token is out", servletResponse);
                throw new ExpiredCredentialsException("token 失效");
            }
            return executeLogin(servletRequest, servletResponse);
        }
    }

    // 重定向方法解决filter内部不能正确捕捉异常，但是有bug 参见https://blog.csdn.net/m0_67391521/article/details/124343856
//    @Override
//    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String token = httpServletRequest.getHeader("Authorization");
//        JwtToken jwtToken = new JwtToken(token);
//        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
//        try {
//            getSubject(request, response).login(jwtToken);
//        } catch (AuthenticationException e) {
//            responseError(response,401,e.getMessage());
//            return false;
//        }
//        // 如果没有抛出异常则代表登入成功，返回true
//        return true;
//    }

//    private void responseError(ServletResponse response, int code,String message) {
//        try {
//            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//            //设置编码，否则中文字符在重定向时会变为空字符串
//            message = URLEncoder.encode(message, "UTF-8");
//            //如果有项目名称路径记得加上
////            httpServletResponse.sendRedirect("/filterError/" + code + "/" + message);
//            httpServletResponse.sendRedirect("/filterError/" + code );
//
//        } catch (IOException e1) {
//            log.error(e1.getMessage());
//        }
//    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.info("onLoginFailure阶段");

        Throwable throwable = e.getCause() == null ? e : e.getCause();
        try {
            Result.failReturnJson(400, throwable.getMessage(), response);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        log.info("preHandle阶段");

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
