package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Venda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendaRepositoryPort {

    Venda save(Venda venda);
    Optional<Venda> findById(UUID id);
    List<Venda> findAll();
}
