package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.consumer;

import com.kavex.xtoke.controle_estoque.application.service.NotaFiscalService;
import com.kavex.xtoke.controle_estoque.infrastructure.queue.RedisEventQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenerVendaEvent {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;
    private final RedisEventQueueService redisEventQueueService;
    private final NotaFiscalService notaFiscalService;

    @EventListener
    public void handleVendaRealizada(EventVendaRealizada event) {
        System.out.println("⚠ Venda: " + event.vendaId() +
                " realizada para o cliente: " + event.clienteId());

        kafkaEventPublisher.publicarEventoVendaRealizada(event.vendaId(), event.clienteId());
        redisEventQueueService.adicionarEventoNaFila("Venda realizada: " + event.vendaId());
        notaFiscalService.gerarNotaFiscal(event.vendaId());
    }
}
