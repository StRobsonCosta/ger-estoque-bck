package com.kavex.xtoke.controle_estoque.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "token:";

    public void salvarToken(String userId, String token, Long expiration) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, expiration, TimeUnit.SECONDS);
    }

    public void salvarToken(String username, String token) {
        log.info("Registrando Token do User: {} em Cache", username);

        redisTemplate.opsForValue().set(PREFIX + username, token);
    }

    public String obterToken(String userId) {
        log.info("Obtendo no Cache o Token do User de ID: {}", userId);

        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void removerToken(String userId) {
        log.info("Removendo do Cache o Token do User de ID: {}", userId);

        redisTemplate.delete(PREFIX + userId);
    }
}
