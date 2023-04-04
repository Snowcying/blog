package com.cxy.shiro;

import com.cxy.entity.User;
import com.cxy.service.UserService;
import com.cxy.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("登录一下");

        JwtToken jwtToken = (JwtToken) token;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(Long.valueOf(userId));

        // 此处抛出的异常会触发onLoginFailure函数
        if(user==null){
            throw new UnknownAccountException("accountNoExist");
        }
        if(user.getStatus()==-1){
            throw new LockedAccountException("accountLocked");
        }
        AccountProfile profile =new AccountProfile();
        BeanUtils.copyProperties(user,profile);

        return new SimpleAuthenticationInfo(profile,jwtToken.getCredentials(),getName());
    }
}
