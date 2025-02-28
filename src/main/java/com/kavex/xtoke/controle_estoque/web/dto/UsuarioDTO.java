package com.kavex.xtoke.controle_estoque.web.dto;

import com.kavex.xtoke.controle_estoque.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsuarioDTO {

    private String email;
    private String senha;
    private Role role;
}
