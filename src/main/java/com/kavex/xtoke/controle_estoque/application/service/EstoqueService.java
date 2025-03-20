package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.in.EstoqueUseCase;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService implements EstoqueUseCase {

    private final ProdutoUseCase produtoService;

    @Override
    public void validarDisponibilidadeEstoque(VendaDTO vendaDTO) {
        log.info("Validando disponibilidade de estoque");

        Map<UUID, Integer> estoques = consultarEstoque(vendaDTO);

        List<String> produtosSemEstoque = vendaDTO.getItens().stream()
                .filter(item -> item.getQuantidade() > estoques.getOrDefault(item.getProdutoId(), 0))
                .map(item -> "Produto ID: " + item.getProdutoId() + " - Estoque disponível: "
                        + estoques.getOrDefault(item.getProdutoId(), 0) + ", Solicitado: " + item.getQuantidade())
                .toList();

        if (!produtosSemEstoque.isEmpty()) {
            throw new IllegalArgumentException(ErroMensagem.ESTOQUES_INSUFICIENTE.getMensagem()
                    + String.join("; ", produtosSemEstoque));
        }
    }

    @Override
    public void atualizarEstoque(Venda venda) {
        log.info("Atualizando estoque para a venda ID: {}", venda.getId());

        Map<UUID, Integer> mapAlteracoes = venda.getItens().stream()
                .peek(ItemVenda::calcularSubtotal)
                .collect(Collectors.toMap(item -> item.getProduto().getId(), item -> -item.getQuantidade()));

        produtoService.atualizarEstoques(mapAlteracoes);
        log.info("Estoque Atualizado ");
    }

    @Override
    public void atualizarEstoque(Venda venda, VendaDTO vendaDTO) {
        log.info("Atualizando estoque no PUT para a venda ID: {}", venda.getId());
        Map<UUID, Integer> mapQuant = vendaDTO.getItens().stream()
                .collect(Collectors.toMap(ItemVendaDTO::getProdutoId, ItemVendaDTO::getQuantidade));

        Map<UUID, Integer> mapAlteracoes = new HashMap<>();

        venda.getItens().forEach(item -> {
            item.calcularSubtotal();

            Integer novaQuant = mapQuant.getOrDefault(item.getProduto().getId(), 0);
            Integer diferencaQuant = novaQuant - item.getQuantidade();

            mapAlteracoes.put(item.getProduto().getId(), -(diferencaQuant));

        });

        produtoService.atualizarEstoques(mapAlteracoes);
        log.info("Estoque Atualizado no PUT");
    }

    private Map<UUID, Integer> consultarEstoque(VendaDTO vendaDTO) {
        log.info("Consultando o Estoque Atual ");
        List<UUID> idsProdutos = vendaDTO.getItens().stream()
                .map(ItemVendaDTO::getProdutoId)
                .toList();
        return produtoService.consultarEstoques(idsProdutos);
    }
}
