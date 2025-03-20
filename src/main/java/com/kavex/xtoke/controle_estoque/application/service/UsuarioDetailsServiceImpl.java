package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Iniciando Carregar usuário pelo username/e-mail: {}", username);

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para username/e-mail: {}", username);
                    return new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem());
                });

        log.info("Usuário encontrado. ID: {}", usuario.getId() + " - UserName: " + username);

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }
}
