package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;

public interface UsuarioUseCase {
    UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO buscarPorEmail(String email);
}
