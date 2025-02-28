package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
}
