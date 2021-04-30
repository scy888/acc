package common;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-03-04 19:50:19
 * @describe: Jwt工具类,创建和解析token
 */
public class JwtUtils {
    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);
    public String createToken(String issuer,String subject,String signature,Long expiration) throws Exception {
        /**
         * @Description: issuer:签发者,subject:主题,signature:签名,expiration:过期时间
         * @methodName: createToken
         * @Param: [issuer, subject, signature, expiration]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/3/4 19:59
         */
        try {
            long inssuedDate = System.currentTimeMillis();
            String compact = Jwts.builder()
                    .setId(UUID.randomUUID().toString()) /** 设置token 的id */
                    .setSubject(subject) /** 设置主题*/
                    .setIssuer(issuer)
                    .setIssuedAt(new Date(inssuedDate)) /** 设置签发时间*/
                    .setExpiration(new Date(inssuedDate + expiration)) /** 设置过期时间*/
                    .signWith(SignatureAlgorithm.HS256, signature) /** 设置签名 */
                    .claim("roles", "roles") /** 设置角色*/
                    .compact();
            return compact;
        } catch (Exception e) {
           logger.info("创建token失败");
           throw new ClaimpptException(e.getMessage());
        }
    }
    public Claims parseToken(String signature,String tokenStr) throws Exception {
        /**
         * @Description: signature:签名,tokenStr:token字符串
         * @methodName: parseToken
         * @Param: [signature, tokenStr]
         * @return: io.jsonwebtoken.Claims
         * @Author: scyang
         * @Date: 2020/3/4 20:42
         */
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signature)
                    .parseClaimsJws(tokenStr)
                    .getBody();
            return claims;
        } catch (Exception e) {
            logger.info("claims{}:","解析token失败");
           throw new ClaimpptException(e.getMessage()+" token过期解析失败");
        }

    }
    private SecretKey getSignatureKey(String signature) throws Exception {
        /**
         * @Description: 签名算法及密钥
         * @methodName: getSignatureKey
         * @Param: [signature]
         * @return: javax.crypto.SecretKey
         * @Author: scyang
         * @Date: 2020/3/4 20:16
         */
        try {
            byte[] bytes = Base64.getEncoder().encode(signature.getBytes("utf-8"));
            SecretKey secretKey=new SecretKeySpec(bytes,0,bytes.length,"AES" );
            return secretKey;
        } catch (Exception e) {
           logger.info("secretKey{}","生成签名算法及密钥失败");
           throw new ClaimpptException(e.getMessage());
        }
    }
    @Test
    public void testCreateToken() throws Exception {
        Map<String,Object> mapSubject=new HashMap<>();
        String status="{\"height\":\"175cm\",\"weight\":\"60kg\"}";
        mapSubject.put("status",status );
        mapSubject.put("roles","roles" );

        String token = createToken("盛重阳", JSON.toJSONString(mapSubject), "itcast", 5*60*1000L);
        System.out.println(token);
    }
    @Test
    public void testParseToken() throws Exception {
        Claims claims = parseToken("itcast", "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMWNhMDVlYy04OTU4LTRlY2MtOWYzOC00OTQ5Njk0MjQwNzEiLCJzdWIiOiJ7XCJyb2xlc1wiOlwicm9sZXNcIixcInN0YXR1c1wiOlwie1xcXCJoZWlnaHRcXFwiOlxcXCIxNzVjbVxcXCIsXFxcIndlaWdodFxcXCI6XFxcIjYwa2dcXFwifVwifSIsImlzcyI6Iuebm-mHjemYsyIsImlhdCI6MTU4MzMzODc1MCwiZXhwIjoxNTgzMzM5MDUwLCJyb2xlcyI6InJvbGVzIn0._E1R9Vih040xLUnGZzf7Y2wtZetMFus_Hfg3JVdjtD4");
        System.out.println("token的id: "+claims.getId());
        System.out.println("token的签发者: "+claims.getIssuer());
        System.out.println("token的签发时间: "+claims.getIssuedAt());
        System.out.println("token的过期时间: "+claims.getExpiration());
        System.out.println("token的角色: "+claims.get("roles"));
        System.out.println("token的主题: "+claims.getSubject());
        System.out.println("token的角色: "+JSON.parseObject(claims.getSubject(),Map.class).get("roles"));
    }
}
