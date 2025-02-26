package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem()));
    }
}
