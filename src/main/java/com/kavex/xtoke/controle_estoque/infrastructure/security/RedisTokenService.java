package com.kavex.xtoke.controle_estoque.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "token:";

    public void salvarToken(String userId, String token, Long expiration) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, expiration, TimeUnit.SECONDS);
    }

    public void salvarToken(String username, String token) {
        redisTemplate.opsForValue().set(PREFIX + username, token);
    }

    public String obterToken(String userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void removerToken(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
