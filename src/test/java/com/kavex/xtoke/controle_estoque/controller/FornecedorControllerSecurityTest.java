package com.kavex.xtoke.controle_estoque.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FornecedorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void devePermitirSalvarFornecedorParaAdmin() throws Exception {
        String fornecedorJson = """
            {
                "nome": "Fornecedor Gêpêtê",
                "cnpj": "12345678000198",
                "email": "fornecedorgpt@empresa.com",
                "fone": "(11) 99999-9999",
                "endereco": "Rua das Empresas, 123 - São Paulo, SP"
            }
        """;

        mockMvc.perform(post("/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deveNegarSalvarFornecedorParaUsuarioComum() throws Exception {
        String fornecedorJson = """
            {
                "nome": "Fornecedor Teste",
                "cnpj": "12345678000199"
            }
        """;

        mockMvc.perform(post("/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAcessoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/fornecedores/listar"))
                .andExpect(status().isUnauthorized());
    }

}
