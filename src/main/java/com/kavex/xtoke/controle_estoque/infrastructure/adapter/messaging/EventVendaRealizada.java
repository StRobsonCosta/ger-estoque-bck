package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.consumer;

import java.util.UUID;

public record EventVendaRealizada(UUID vendaId, UUID clienteId) {
}
