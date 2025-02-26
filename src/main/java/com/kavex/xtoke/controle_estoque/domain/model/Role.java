package com.kavex.xtoke.controle_estoque.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusVenda {
    PENDENTE("Venda pendente"),
    CONCLUIDA("Venda concluída"),
    CANCELADA("Venda cancelada");

    private final String descricao;

    public static StatusVenda fromString(String value) {
        for (StatusVenda status : StatusVenda.values()) {
            if (status.name().equalsIgnoreCase(value))
                return status;
        }
        throw new IllegalArgumentException("Status de venda inválido: " + value);
    }

}
