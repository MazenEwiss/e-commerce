package com.mazen.ecommerce.wallet_service.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    public JwtService(@Value("${jwt.private-key}") String privateKeyStr,
                       @Value("${jwt.public-key}") String publicKeyStr) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
        this.privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));

        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        this.publicKey = factory.generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(privateKey)
                .compact();
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