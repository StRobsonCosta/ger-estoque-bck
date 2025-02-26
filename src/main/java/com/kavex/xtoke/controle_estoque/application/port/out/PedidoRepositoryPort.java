package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Pedido;

public interface PedidoRepositoryPort {

    Pedido save(Pedido pedido);
}
