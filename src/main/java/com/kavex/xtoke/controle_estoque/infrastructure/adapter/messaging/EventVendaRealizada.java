package com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging;

import java.util.UUID;

public record EventVendaRealizada(UUID vendaId, UUID clienteId) {
}
