package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.UsuarioMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.UsuarioUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("usuarioDetailsService")
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        log.info("Iniciando criação de usuário com e-mail: {}", usuarioDTO.getEmail());

        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            log.warn("Tentativa de cadastro com e-mail já existente: {}", usuarioDTO.getEmail());
            throw new IllegalArgumentException(ErroMensagem.EMAIL_JA_CADASTRADO.getMensagem());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setRole(usuarioDTO.getRole());

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso. ID: {}", usuario.getId());

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO buscarPorEmail(String email) {
        log.info("Buscando usuário com e-mail: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para e-mail: {}", email);
                    return new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem());
                });

        log.info("Usuário encontrado. ID: {}", usuario.getId());
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO atualizarUsuario(UUID id, UsuarioDTO usuarioDTO) {
        log.info("Iniciando atualização do usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualização de usuário inexistente. ID: {}", id);
                    return new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem());
                });

        usuarioMapper.updateFromDTO(usuarioDTO, usuario);
        usuarioRepository.save(usuario);

        log.info("Usuário atualizado com sucesso. ID: {}", id);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public void desativarUsuario(String email) {
        log.info("Iniciando desativação do usuário com e-mail: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de desativação de usuário inexistente. E-mail: {}", email);
                    return new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem());
                });
        usuario.setAtivo(Boolean.FALSE);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso. E-mail: {}", email);
    }
}
