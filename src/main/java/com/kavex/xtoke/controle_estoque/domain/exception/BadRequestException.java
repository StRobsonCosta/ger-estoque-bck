package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseException {

    public BadRequestException(ErroMensagem erroMensagem) {
        super(erroMensagem);
    }
}
