package com.kavex.xtoke.controle_estoque.application.port.out;

import com.kavex.xtoke.controle_estoque.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(UUID id);
    Usuario save(Usuario usuario);
}
