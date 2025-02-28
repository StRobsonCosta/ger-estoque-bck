package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import com.kavex.xtoke.controle_estoque.application.service.PedidoService;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.notification.EmailNotificationAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListenerEstoqueEvent {

    private final KafkaEventPublisherAdapter kafkaEventPublisher;
    private final PedidoService pedidoService;
    private final EmailNotificationAdapter emailNotificationAdapter;

    @EventListener
    public void handleEstoqueBaixo(EventEstoqueBaixo event) {
        log.warn("⚠ Estoque baixo para o produto {}! Quantidade atual: {}", event.produtoId(), event.quantidadeAtual());

        System.out.println("⚠ Estoque baixo para o produto: " + event.produtoId() +
                " | Quantidade atual: " + event.quantidadeAtual());

        kafkaEventPublisher.publicarEventoEstoqueBaixo(event.nomeProduto(), event.produtoId(), event.quantidadeAtual());

        String mensagem = String.format("""
            <h2>Alerta de Estoque Baixo</h2>
            <p>O produto <strong>%s</strong> (ID: %s) atingiu um estoque crítico.</p>
            <p>Quantidade atual: <strong>%d</strong></p>
            <p>Reabasteça o estoque o quanto antes.</p>
        """, event.nomeProduto(), event.produtoId(), event.quantidadeAtual());

        emailNotificationAdapter.enviarEmail("estoque@empresa.com", "⚠ Alerta: Estoque Baixo", mensagem);

        //Cuidado
        pedidoService.criarPedidoReposicao(event.produtoId());
    }
}
