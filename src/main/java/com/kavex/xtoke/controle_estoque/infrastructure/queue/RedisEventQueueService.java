package com.kavex.xtoke.controle_estoque.infrastructure.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisEventQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_NAME = "eventosVenda";

    public void adicionarEventoNaFila(String evento) {
        redisTemplate.opsForList().rightPush(QUEUE_NAME, evento);
    }

    public String consumirEvento() {
        return redisTemplate.opsForList().leftPop(QUEUE_NAME);
    }

}
