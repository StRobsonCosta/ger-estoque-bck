package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.service.EstoqueService;
import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    @Mock
    private ProdutoUseCase produtoService;

    @InjectMocks
    private EstoqueService estoqueService;

    @Test
    void deveValidarEstoqueComSucesso() {
        VendaDTO vendaDTO = criarVendaDTO(List.of(
                new ItemVendaDTO(UUID.randomUUID(), 2, new BigDecimal(1), new BigDecimal(2))
        ));

        Map<UUID, Integer> estoqueSimulado = new HashMap<>();
        vendaDTO.getItens().forEach(item -> estoqueSimulado.put(item.getProdutoId(), 5));

        when(produtoService.consultarEstoques(anyList())).thenReturn(estoqueSimulado);

        assertDoesNotThrow(() -> estoqueService.validarDisponibilidadeEstoque(vendaDTO));
        verify(produtoService).consultarEstoques(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueForInsuficiente() {
        UUID produtoId = UUID.randomUUID();
        VendaDTO vendaDTO = criarVendaDTO(List.of(
                new ItemVendaDTO(produtoId, 10, new BigDecimal(1), new BigDecimal(10))
        ));

        when(produtoService.consultarEstoques(anyList()))
                .thenReturn(Map.of(produtoId, 2));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                estoqueService.validarDisponibilidadeEstoque(vendaDTO));

        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        verify(produtoService).consultarEstoques(anyList());
    }

    @Test
    void deveAtualizarEstoqueComVenda() {
        UUID produtoId = UUID.randomUUID();
        Produto produto = new Produto();
        produto.setId(produtoId);

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(3);

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setItens(List.of(item));

        estoqueService.atualizarEstoque(venda);

        Map<UUID, Integer> expectedAlteracoes = Map.of(produtoId, -3);
        verify(produtoService).atualizarEstoques(eq(expectedAlteracoes));
    }

    @Test
    void deveAtualizarEstoqueComVendaEDTO() {
        UUID produtoId = UUID.randomUUID();

        Produto produto = new Produto();
        produto.setId(produtoId);

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(4);
        item.setPrecoUnitario(new BigDecimal(1));

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setItens(List.of(item));

        VendaDTO vendaDTO = criarVendaDTO(List.of(
                new ItemVendaDTO(produtoId, 6, new BigDecimal(1), new BigDecimal(6))
        ));

        estoqueService.atualizarEstoque(venda, vendaDTO);

        Map<UUID, Integer> expectedAlteracoes = Map.of(produtoId, -2);
        verify(produtoService).atualizarEstoques(eq(expectedAlteracoes));
    }

    private VendaDTO criarVendaDTO(List<ItemVendaDTO> itens) {
        VendaDTO vendaDTO = new VendaDTO();
        vendaDTO.setItens(itens);
        return vendaDTO;
    }
}
