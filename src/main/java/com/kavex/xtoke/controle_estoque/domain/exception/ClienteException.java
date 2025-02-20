package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteException extends RuntimeException {

    private final String produtoId;
    private final Integer quantDisponivel;
    private final Integer quantRequerida;

    public ClienteException(String produtoId, Integer quantDisponivel, Integer quantRequerida) {
        super(String.format("Estoque insuficiente para o produto ID %s. Disponível: %d, Requerido: %d",
                produtoId, quantDisponivel, quantRequerida));
        this.produtoId = produtoId;
        this.quantDisponivel = quantDisponivel;
        this.quantRequerida = quantRequerida;
    }
}
