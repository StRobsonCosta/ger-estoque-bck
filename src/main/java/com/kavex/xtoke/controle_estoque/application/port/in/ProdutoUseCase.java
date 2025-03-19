package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
//import jakarta.persistence.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProdutoUseCase {

    void atualizarEstoques(Map<UUID, Integer> mapEstoque);

    void atualizarEstoque(UUID produtoId, Integer quantidadeAlteracao);

    @Cacheable(value = "produtos", key = "#id")
    ProdutoDTO buscarPorId(UUID id);

    @CachePut(value = "produtos", key = "#produtoDTO.id")
    ProdutoDTO salvar(ProdutoDTO produtoDTO);

    ProdutoDTO atualizar(UUID produtoId, ProdutoDTO produtoDTO);

    List<ProdutoDTO> listarProdutos();

    @CacheEvict(value = "produtos", key = "#id")
    void removerProduto(UUID id);

    Map<UUID, Integer> consultarEstoques(List<UUID> produtoIds);
}
