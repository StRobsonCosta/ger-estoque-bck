package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListenerEstoqueEvent {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;

    @EventListener
    public void handleEstoqueBaixo(EventEstoqueBaixo event) {
        System.out.println("⚠ Estoque baixo para o produto: " + event.produtoId() +
                " | Quantidade atual: " + event.quantidadeAtual());

        kafkaEventPublisher.publicarEventoEstoqueBaixo(event.produtoId(), event.quantidadeAtual());
    }
}
