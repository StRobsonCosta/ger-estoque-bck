package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Produto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepositoryPort {

    Produto save(Produto produto);
    Optional<Produto> findById(UUID id);
    List<Produto> findAll();
    void deleteById(UUID id);
}
