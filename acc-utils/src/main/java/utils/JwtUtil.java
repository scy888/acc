package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 * jwt工具类  创建token和解析token
 */
@ConfigurationProperties("jwt.config")
public class JwtUtil {

    private String key ; //秘钥

    private long ttl ;//一个小时token过期

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * 生成JWT
     *
     * @param id  用户id
     * @param subject 用户名
     * @param roles 角色 登录的时候设置角色
     * @return  token
     */
    public String createJWT(String id, String subject, String roles) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setId(id)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key).claim("roles", roles);
        //ttl过期时间
        if (ttl > 0) {
            builder.setExpiration( new Date( nowMillis + ttl));
        }
        return builder.compact();//生成token
    }

    /**
     * 解析JWT
     * @param jwtStr  token字符串（从客户端中头信息中获取的）
     * @return
     */
    public Claims parseJWT(String jwtStr){
        return  Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwtStr)
                .getBody(); //body：载荷信息 ：有效信息
    }

}