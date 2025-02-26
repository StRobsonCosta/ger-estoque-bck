package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import com.kavex.xtoke.controle_estoque.infrastructure.queue.RedisEventQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter {

    private final KafkaTemplate<String, EventEstoqueBaixo> kafkaTemplateEstoque;
    private final KafkaTemplate<String, EventPedidoCriado> kafkaTemplatePedido;
    private final KafkaTemplate<String, EventVendaRealizada> kafkaTemplateVenda;
    private final RedisEventQueueService redisEventQueueService;
    private final KafkaTemplate<String, String> kafkaTemplateRedis;

    public void publicarEventoEstoqueBaixo(UUID produtoId, Integer quantidadeAtual) {
        EventEstoqueBaixo event = new EventEstoqueBaixo(produtoId, quantidadeAtual);

        kafkaTemplateEstoque.send("ESTOQUE-BAIXO-TOPIC", event);
        System.out.println("📤 Evento enviado para Kafka: " + event);
    }

    public void publicarEventoPedidoCriado(UUID pedidoId, UUID clienteId) {
        EventPedidoCriado event = new EventPedidoCriado(pedidoId, clienteId);
        kafkaTemplatePedido.send("PEDIDO-CRIADO-TOPIC", event);
        System.out.println("📤 Evento de pedido criado enviado para Kafka: " + event);
    }

    public void publicarEventoVendaRealizada(UUID vendaId, UUID clienteId) {
        EventVendaRealizada event = new EventVendaRealizada(vendaId, clienteId);
        kafkaTemplateVenda.send("VENDA-REALIZADA-TOPIC", event);
        System.out.println("📤 Evento de venda realizada enviado para Kafka: " + event);
    }

    @Scheduled(fixedRate = 5000) // Executa a cada 5 segundos
    public void processarEventos() {
        String evento;
        while ((evento = redisEventQueueService.consumirEvento()) != null) {
            kafkaTemplateRedis.send("VENDAS-TOPIC", evento);
        }
    }
}
