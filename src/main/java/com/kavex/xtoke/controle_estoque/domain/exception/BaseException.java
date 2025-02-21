package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String mensagem;

    public BaseException(ErroMensagem erroMensagem) {
        super(erroMensagem.getMensagem());
        this.status = erroMensagem.getStatus();
        this.mensagem = erroMensagem.getMensagem();
    }
}
