package com.cxy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * jwt工具类
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "cxy.jwt")
public class JwtUtils {

    private String secret;
    private long expire;
    private String header;

    /**
     * 生成jwt token
     */
    public String generateToken(long userId, Date issuedAt) {
//        Date nowDate = new Date();
        Date nowDate = issuedAt;
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
//        log.info(secret);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId + "")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 还可以验证合法性
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.debug("validate is token error ", e);
            throw e;
//            return null;
        }
    }

    public String getSign(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getSignature();
        } catch (Exception e) {
            log.debug("validate is token error ", e);
            return null;
        }
    }

    public boolean getValid(String userToken) {
        Claims claim = getClaimByToken(userToken);
        Long id = Long.valueOf(claim.getSubject());
        System.out.println("id" + id);
        String myToken = generateToken(id, claim.getIssuedAt());
        System.out.println("my:" + myToken);
        System.out.println("user:" + userToken);
        return Objects.equals(myToken, userToken);
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}