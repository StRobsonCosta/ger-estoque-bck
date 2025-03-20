package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import org.springframework.stereotype.Repository;

import java.util.*;

public interface ProdutoRepositoryPort {

    Produto save(Produto produto);
    Optional<Produto> findById(UUID id);
    List<Produto> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByNome(String nome);
    Optional<Integer> findEstoqueById(UUID id);
    List<Object[]> findEstoquesByIds(List<UUID> ids);
    boolean existsByFornecedorId(UUID fornecedorId);
}
