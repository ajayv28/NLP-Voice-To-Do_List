package com.ajay.nlp_voice_todo_list.config;

import com.ajay.nlp_voice_todo_list.common.StringConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Configuration
public class JwtConfig {

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getRSAPublicKey())
                .build();
    }

    private RSAPublicKey getRSAPublicKey() {
        try {
            String publicKeyEnhanced = StringConstants.PUBLIC_KEY
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyEnhanced);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }


    public Claims parseToken(String token) {
        try {
            return jwtParser().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }


    public String getUserRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }


    public String getUserEmail(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }


    public String getUserPassword(String token) {
        Claims claims = parseToken(token);
        return claims.get("password", String.class);
    }


    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }
}
