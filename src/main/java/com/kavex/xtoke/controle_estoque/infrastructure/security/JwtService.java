package com.kavex.xtoke.controle_estoque.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;

@Slf4j
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
        log.info("Gerando Token para User de ID: {}", userId);
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        log.info("Validando Token para User: {}", userDetails.getUsername());
        try {
            String username = extrairUserId(token); // Extrai o usuário do token
            return username.equals(userDetails.getUsername()) && !tokenExpirado(token);
        } catch (Exception e) {
            log.error("Não foi possível validar User: {}, ou Token Expirado.", userDetails.getUsername(), e);
            return false;
        }
    }

    public boolean validarToken(String token) {
        log.info("Validação Simples de Token");
        try {
            return !tokenExpirado(token); // Apenas verifica a expiração
        } catch (Exception e) {
            return false;
        }
    }

    public String extrairUserId(String token) {
        log.info("Em JwtService extrairUserId)");
        return extrairClaim(token).getSubject();
    }

    public boolean tokenExpirado(String token) {
        log.info("Em JwtService tokenExpirado)");
        return extrairClaim(token).getExpiration().before(new Date());
    }

    private Claims extrairClaim(String token) {
        log.info("Extraindo Claim do Token");
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    public String extrairToken(HttpServletRequest request) {
        log.info("Extraindo Token do Header da Requisição (Authorization e Bearer)");

        String bearerToken = request.getHeader("Authorization");
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        log.info("Token Null");
        return null;
    }
}
