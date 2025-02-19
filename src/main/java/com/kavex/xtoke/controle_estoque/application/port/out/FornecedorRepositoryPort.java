package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FornecedorRepositoryPort {

    Fornecedor save(Fornecedor fornecedor);
    Optional<Fornecedor> findById(UUID id);
    List<Fornecedor> findAll();
    void deleteById(UUID id);
}
