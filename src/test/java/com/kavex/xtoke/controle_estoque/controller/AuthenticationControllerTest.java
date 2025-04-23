package com.kavex.xtoke.controle_estoque.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kavex.xtoke.controle_estoque.infrastructure.security.JwtAuthenticationFilter;
import com.kavex.xtoke.controle_estoque.infrastructure.security.JwtService;
import com.kavex.xtoke.controle_estoque.infrastructure.security.RedisTokenService;
import com.kavex.xtoke.controle_estoque.web.AuthenticationController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    public static final String EMAIL = "user@example.com";
    public static final String URL_LOGOUT = "/auth/logout";
    public static final String URL_LOGIN = "/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RedisTokenService redisTokenService;

    @Autowired
    private ObjectMapper objectMapper;

//    @BeforeEach
//    void setup() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
//                .addFilters(jwtAuthenticationFilter)  // Adiciona o filtro JWT
//                .build();
//    }


    @Test
    @WithMockUser(username = EMAIL, password = "password", roles = "ADMIN")
    void deveRealizarLoginComSucesso() throws Exception {
        String password = "password";
        String token = "mock-jwt-token";
        String userId = "user123";

        // Mock do comportamento do JwtService
        when(jwtService.extrairToken(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtService.validarToken(token)).thenReturn(true);
        when(jwtService.extrairUserId(token)).thenReturn(userId);

        // Mock do comportamento do RedisTokenService
        when(redisTokenService.obterToken(userId)).thenReturn(token);

        // Simula o comportamento do UserDetailsService
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList()); // Aqui evitamos o NullPointerException
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);

        // Simula o comportamento do AuthenticationManager
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails); // Garante que retorna userDetails
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Simula o comportamento do JwtService
        when(jwtService.gerarToken(userDetails.getUsername())).thenReturn(token);

        // Simula o comportamento do RedisTokenService
        doNothing().when(redisTokenService).salvarToken(EMAIL, token);

        mockMvc.perform(post(URL_LOGIN)
                        .param("email", EMAIL)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        // Verificar se os métodos foram chamados
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userDetailsService, times(1)).loadUserByUsername(EMAIL);
        verify(jwtService, times(1)).gerarToken(EMAIL);
        verify(redisTokenService, times(1)).salvarToken(EMAIL, token);
    }

    @Test
    void deveRealizarLogoutComSucesso() throws Exception {

        // Simula o comportamento do RedisTokenService
        doNothing().when(redisTokenService).removerToken(EMAIL);

        mockMvc.perform(post(URL_LOGOUT)
                        .param("email", EMAIL))
                .andExpect(status().isOk());

        // Verificar se o método foi chamado
        verify(redisTokenService, times(1)).removerToken(EMAIL);
    }

    @Test
    void naoDevePermitirLogoutSemUsuarioAutenticado() throws Exception {

        mockMvc.perform(post(URL_LOGOUT)
                        .param("email", EMAIL))
                .andExpect(status().isForbidden());  // Espera-se falha 403 para usuário não autenticado
    }
}

