package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;

import java.util.UUID;

public interface UsuarioUseCase {
    UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO buscarPorEmail(String email);

    UsuarioDTO atualizarUsuario(UUID id, UsuarioDTO usuarioDTO);

    void desativarUsuario(String email);
}
