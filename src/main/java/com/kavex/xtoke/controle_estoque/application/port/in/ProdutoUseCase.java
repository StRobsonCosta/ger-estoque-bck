package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;

import java.util.List;
import java.util.UUID;

public interface ProdutoUseCase {

    void atualizarEstoque(UUID produtoId, Integer quantidadeAlteracao);

    ProdutoDTO buscarPorId(UUID id);

    ProdutoDTO salvar(ProdutoDTO produtoDTO);

    List<ProdutoDTO> listarProdutos();

    void removerProduto(UUID id);
}
