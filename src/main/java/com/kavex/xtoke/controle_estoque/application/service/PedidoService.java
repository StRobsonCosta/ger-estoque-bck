package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.in.PedidoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.PedidoRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Pedido;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventPedidoCriado;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.KafkaEventPublisherAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService implements PedidoUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final KafkaEventPublisherAdapter kafkaEventPublisher;
    private final ProdutoRepositoryPort produtoRepository;
    private final FornecedorRepositoryPort fornecedorRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void criarPedidoReposicao(UUID produtoId, Integer quant) {
        log.info("Iniciando criação de pedido de reposição para o produto ID: {}", produtoId);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> {
                    log.warn("❌ Produto não encontrado para ID: {}", produtoId);
                    return new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
                });

        Fornecedor fornecedor = fornecedorRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> {
                    log.warn("❌ Fornecedor não encontrado para o produto ID: {}", produtoId);
                    return new NotFoundException(ErroMensagem.FORNECEDOR_NAO_ENCONTRADO);
                });

        ItemVenda itemVenda = new ItemVenda(
                null,
                produto,
                quant,
                produto.getPreco(),
                BigDecimal.ZERO
        );

        // Cria o Pedido com o fornecedor e o item
        Pedido pedido = new Pedido();
        log.info("Pedido {} criado com sucesso para o fornecedor {}.", pedido.getId(), fornecedor.getId());

        pedido.setFornecedor(fornecedor);
        pedido.setItens(Collections.singletonList(itemVenda));

        // Salva o pedido
        pedidoRepository.save(pedido);

        // 🔥 Publica evento de pedido criado
        eventPublisher.publishEvent(new EventPedidoCriado(pedido.getId(), fornecedor.getId()));
        log.info("Evento de pedido criado publicado para ID: {}", pedido.getId());

    }

//    public void enviarPedidoParaFornecedor(UUID fornecedorId, UUID pedidoId) {
//        String url = "https://api.fornecedor.com/pedidos";
//        PedidoFornecedorDTO pedidoFornecedor = new PedidoFornecedorDTO(fornecedorId, pedidoId);
//
//        restTemplate.postForObject(url, pedidoFornecedor, Void.class);
//
//        System.out.println("📤 Pedido enviado ao fornecedor via API.");
//    }
}
