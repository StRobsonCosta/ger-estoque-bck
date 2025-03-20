package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);
    Optional<Cliente> findById(UUID id);
    List<Cliente> findAll();
    void deleteById(UUID id);

    boolean existsByCpf(String cpf);
    boolean existsById(UUID id);
}
