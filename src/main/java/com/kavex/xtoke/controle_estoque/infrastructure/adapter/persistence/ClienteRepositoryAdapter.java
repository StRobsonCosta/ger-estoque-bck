package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.ClienteRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepositoryAdapter extends JpaRepository<Cliente, UUID>, ClienteRepositoryPort {

}
