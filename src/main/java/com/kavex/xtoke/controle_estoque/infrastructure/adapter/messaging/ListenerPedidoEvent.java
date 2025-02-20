package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenerPedidoEvent {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;

    @EventListener
    public void handlePedidoCriado(EventPedidoCriado event) {
        System.out.println("⚠ Pedido do produto: " + event.pedidoId() +
                " foi criado para o cliente: " + event.clienteId());

        kafkaEventPublisher.publicarEventoPedidoCriado(event.pedidoId(), event.clienteId());
    }
}
