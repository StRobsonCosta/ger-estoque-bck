package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepositoryAdapter extends JpaRepository<Produto, UUID>, ProdutoRepositoryPort {

    @Query("SELECT p.id, p.estoque FROM Produto p WHERE p.id IN :ids")
    List<Object[]> findEstoquesByIds(@Param("ids") List<UUID> ids);

}
