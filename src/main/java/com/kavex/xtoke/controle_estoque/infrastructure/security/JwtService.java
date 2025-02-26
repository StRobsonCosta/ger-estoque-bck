package com.kavex.xtoke.controle_estoque.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private JwtParser getJwtParser() {
        return Jwts.parser().verifyWith(getSigningKey()).build();
    }

    public String gerarToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        try {
            String username = extrairUserId(token); // Extrai o usuário do token
            return username.equals(userDetails.getUsername()) && !tokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarToken(String token) {
        try {
            return !tokenExpirado(token); // Apenas verifica a expiração
        } catch (Exception e) {
            return false;
        }
    }

    public String extrairUserId(String token) {
        return extrairClaim(token).getSubject();
    }

    public boolean tokenExpirado(String token) {
        return extrairClaim(token).getExpiration().before(new Date());
    }

    private Claims extrairClaim(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Extrair Token do Header da Requisição
    public String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
