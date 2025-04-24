package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.mapper.UsuarioMapper;
import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.service.UsuarioService;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.Role;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import com.kavex.xtoke.controle_estoque.web.dto.UsuarioDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    @Qualifier("usuarioDetailsService")
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {
        // Arrange
        UsuarioDTO usuarioDTO = new UsuarioDTO("email@email.com", "senha123", Role.USER);
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setEmail(usuarioDTO.getEmail());
        usuarioEntity.setSenha("senhaHash");
        usuarioEntity.setRole(usuarioDTO.getRole());

        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioDTO.getSenha())).thenReturn("senhaHash");
        when(usuarioRepository.save(any())).thenReturn(usuarioEntity);
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = usuarioService.criarUsuario(usuarioDTO);

        // Assert
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(usuarioRepository).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        UsuarioDTO usuarioDTO = new UsuarioDTO("email@existe.com", "senha123", Role.ADMIN);
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(new Usuario()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.criarUsuario(usuarioDTO)
        );
        assertEquals(ErroMensagem.EMAIL_JA_CADASTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void deveBuscarUsuarioPorEmailComSucesso() {
        // Arrange
        String email = "usuario@teste.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        UsuarioDTO dtoEsperado = new UsuarioDTO(email, "senha123", Role.USER);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(dtoEsperado);

        // Act
        UsuarioDTO result = usuarioService.buscarPorEmail(email);

        // Assert
        assertEquals(email, result.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoBuscarUsuarioPorEmailInexistente() {
        // Arrange
        String email = "inexistente@email.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioService.buscarPorEmail(email)
        );
        assertEquals(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        UsuarioDTO usuarioDTO = new UsuarioDTO("novo@email.com", "novaSenha", Role.USER);
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = usuarioService.atualizarUsuario(id, usuarioDTO);

        // Assert
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(usuarioMapper).updateFromDTO(usuarioDTO, usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarUsuarioInexistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        UsuarioDTO usuarioDTO = new UsuarioDTO("teste@email.com", "senha", Role.ADMIN);

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioService.atualizarUsuario(id, usuarioDTO)
        );
        assertEquals(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void deveDesativarUsuarioComSucesso() {
        // Arrange
        String email = "ativo@email.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.desativarUsuario(email);

        // Assert
        assertFalse(usuario.getAtivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoDesativarUsuarioInexistente() {
        // Arrange
        String email = "inexistente@email.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioService.desativarUsuario(email)
        );
        assertEquals(ErroMensagem.USUARIO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }
}
