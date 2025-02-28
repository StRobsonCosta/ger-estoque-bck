package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.UsuarioMapper;
import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.UsuarioUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Qualifier("usuarioDetailsService")
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Usuario usuario = usuarioRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem()));
//
////        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);
//        return new User(
//                usuario.getEmail(),
//                usuario.getSenha(),
//                List.of(new SimpleGrantedAuthority(usuario.getRole().name())) // Exemplo: ROLE_ADMIN, ROLE_USER
//        );
//    }

    @Override
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setRole(usuarioDTO.getRole());

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem()));
        return usuarioMapper.toDTO(usuario);
    }
}
