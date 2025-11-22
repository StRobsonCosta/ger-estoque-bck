package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.port.out.UsuarioRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.service.UsuarioDetailsServiceImpl;
import com.kavex.xtoke.controle_estoque.domain.model.Role;
import com.kavex.xtoke.controle_estoque.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioDetailsServiceImplTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private UsuarioDetailsServiceImpl usuarioDetailsService;

    @Test
    void deveRetornarUserDetailsQuandoUsuarioExiste() {
        // Arrange
        String email = "teste@email.com";
        String senha = "senha123";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setRole(Role.ADMIN);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

        // Assert
        assertEquals(email, userDetails.getUsername());
        assertEquals(senha, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Arrange
        String email = "naoexiste@email.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername(email)
        );

        assertEquals("Usuário Não Encontrado.", exception.getMessage());
    }
}
