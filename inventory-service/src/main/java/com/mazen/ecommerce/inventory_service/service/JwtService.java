package com.mazen.ecommerce.inventory_service.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    private final PublicKey publicKey;
    public JwtService(@Value("${jwt.public-key}") String publicKeyStr) throws Exception {
        // Initialize the service with the public key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
    }
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
    
}
