package com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence;

import com.kavex.xtoke.controle_estoque.application.port.out.PedidoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepositoryAdapter extends JpaRepository<Pedido, UUID>, PedidoRepositoryPort {
}
