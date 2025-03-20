package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.PedidoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PedidoRepositoryAdapter extends JpaRepository<Pedido, UUID>, PedidoRepositoryPort {
}
