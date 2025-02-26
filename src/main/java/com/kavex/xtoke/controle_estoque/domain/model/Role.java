package com.kavex.xtoke.controle_estoque.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMIN,
    CLIENTE,
    FORNECEDOR,
    USER;

    @Override
    public String getAuthority() {
        return name();
    }

}
