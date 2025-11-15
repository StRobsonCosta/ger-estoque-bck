package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.consumer;

import com.kavex.xtoke.controle_estoque.infrastructure.queue.RedisEventQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenerNotaFiscal {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;
    private final RedisEventQueueService redisEventQueueService;

    @EventListener
    public void handleNotaFiscalGerada(EventVendaRealizada event) {
        System.out.println("⚠ Nota fiscal da Venda: " + event.vendaId() +
                " foi Gerada ");

        kafkaEventPublisher.publicarEventoNotaFiscalGerada(event.vendaId());
    }
}
