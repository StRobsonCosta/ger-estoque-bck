package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenerVendaEvent {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;

    @EventListener
    public void handleVendaRealizada(EventVendaRealizada event) {
        System.out.println("⚠ Venda: " + event.vendaId() +
                " realizada para o cliente: " + event.clienteId());

        kafkaEventPublisher.publicarEventoVendaRealizada(event.vendaId(), event.clienteId());
    }
}
