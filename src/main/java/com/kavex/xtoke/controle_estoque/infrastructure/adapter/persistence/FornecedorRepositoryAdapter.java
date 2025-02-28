package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FornecedorRepositoryAdapter extends JpaRepository<Fornecedor, UUID>, FornecedorRepositoryPort {

    @Query("SELECT f FROM Fornecedor f JOIN f.produtos p WHERE p.id = :produtoId")
    Optional<Fornecedor> findByProdutoId(@Param("produtoId") UUID produtoId);
}
