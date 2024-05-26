package org.shareio.gateway.config.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Claims getALlClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();
    }

}
