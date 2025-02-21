package com.kavex.xtoke.controle_estoque.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BaseException {

    public ForbiddenException(ErroMensagem erroMensagem) {
        super(erroMensagem);
    }
}
