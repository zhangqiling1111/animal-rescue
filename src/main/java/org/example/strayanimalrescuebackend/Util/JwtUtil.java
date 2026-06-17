package org.example.strayanimalrescuebackend.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

//    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // 使用安全的密钥生成方法

    // 生成JWT token
    public static String generateToken(String userName) {
        final String SECRET_KEY = "javaxcryptospecSecretfjdzjdzhirKeySpec5555880d8c";

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)  // 使用密钥进行签名
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1小时过期
                .compact();
    }

    // 从JWT token中提取用户名
    public static String extractUsername(String token) {
        return parseJwtToken(token).getSubject();
    }

    // 从JWT token中提取claims
    public static Claims parseJwtToken(String token) {
        final String SECRET_KEY = "javaxcryptospecSecretfjdzjdzhirKeySpec5555880d8c";

        Claims claims = Jwts.parserBuilder()   // 创建 JwtParserBuilder
                .setSigningKey(SECRET_KEY) // 设置密钥
                .build() // 使用 build() 方法构建 JwtParser
                .parseClaimsJws(token)
                .getBody();

        System.out.println("解析出来的claims:"+claims);
        return claims;
    }

    // 验证JWT token是否有效
    public static boolean isTokenExpired(String token) {
        return parseJwtToken(token).getExpiration().before(new Date());
    }

    // 验证Token是否有效
    public static boolean validateToken(String token, String userName) {
        return (userName.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
