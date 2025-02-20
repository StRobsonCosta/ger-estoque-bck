package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter {

    private final KafkaTemplate<String, EventEstoqueBaixo> kafkaTemplateEstoque;
    private final KafkaTemplate<String, EventPedidoCriado> kafkaTemplatePedido;
    private final KafkaTemplate<String, EventVendaRealizada> kafkaTemplateVenda;

    public void publicarEventoEstoqueBaixo(UUID produtoId, Integer quantidadeAtual) {
        EventEstoqueBaixo event = new EventEstoqueBaixo(produtoId, quantidadeAtual);

        kafkaTemplateEstoque.send("ESTOQUE-BAIXO-TOPIC", event);
        System.out.println("📤 Evento enviado para Kafka: " + event);
    }

    public void publicarEventoPedidoCriado(UUID pedidoId, UUID clienteId) {
        EventPedidoCriado event = new EventPedidoCriado(pedidoId, clienteId);
        kafkaTemplatePedido.send("pedido-criado-topic", event);
        System.out.println("📤 Evento de pedido criado enviado para Kafka: " + event);
    }

    public void publicarEventoVendaRealizada(UUID vendaId, UUID clienteId) {
        EventVendaRealizada event = new EventVendaRealizada(vendaId, clienteId);
        kafkaTemplateVenda.send("venda-realizada-topic", event);
        System.out.println("📤 Evento de venda realizada enviado para Kafka: " + event);
    }
}
