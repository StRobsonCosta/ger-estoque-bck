package com.kavex.xtoke.controle_estoque.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProdutoException extends RuntimeException {
    public ProdutoException(String produtoId) {
        super(String.format("Produto com ID %s não encontrado.", produtoId));
    }
}
