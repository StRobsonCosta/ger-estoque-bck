//package com.kavex.xtoke.controle_estoque.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
//import com.kavex.xtoke.controle_estoque.domain.model.Produto;
//import com.kavex.xtoke.controle_estoque.infrastructure.security.JwtAuthenticationFilter;
//import com.kavex.xtoke.controle_estoque.web.AuthenticationController;
//import com.kavex.xtoke.controle_estoque.web.ProdutoController;
//import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
//import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("test")
//public class ProdutoControllerSecurityTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @InjectMocks
//    private ProdutoController produtoController;
//
//    @Mock
//    private ProdutoUseCase produtoUseCase;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private static ProdutoDTO getProdutoDTO() {
//        return ProdutoDTO.builder()
//                .id(UUID.randomUUID())
//                .nome("Produto Teste")
//                .preco(BigDecimal.valueOf(100))
//                .estoque(50)
//                .build();
//    }
//
//    private static FornecedorDTO getFornecedorDTO() {
//        return FornecedorDTO.builder()
//                .id(UUID.randomUUID())
//                .nome("Fornecedor Teste")
//                .email("fornecedor@email.com")
//                .cnpj("12345678000123")
//                .fone("11 2222-2222")
//                .endereco("Rua das Empresa, 28")
//                .build();
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminBuscarProdutoPorId() throws Exception {
//        UUID produtoId = UUID.randomUUID();
//        ProdutoDTO produtoDTO = getProdutoDTO();
//
//        when(produtoUseCase.buscarProdutoDtoPorId(produtoId)).thenReturn(produtoDTO);
//
//        mockMvc.perform(get("/produtos")
//                        .param("id", produtoId.toString())
//                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(produtoId.toString()))
//                .andExpect(jsonPath("$.nome").value(produtoDTO.getNome()));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminListarProdutos() throws Exception {
//        ProdutoDTO produtoDTO1 = getProdutoDTO();
//        ProdutoDTO produtoDTO2 = getProdutoDTO();
//        when(produtoUseCase.listarProdutos()).thenReturn(List.of(produtoDTO1, produtoDTO2));
//
//        mockMvc.perform(get("/produtos/listar")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(produtoDTO1.getId().toString()))
//                .andExpect(jsonPath("$[1].id").value(produtoDTO2.getId().toString()));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminSalvarProduto() throws Exception {
//        ProdutoDTO produtoDTO = getProdutoDTO();
//        when(produtoUseCase.salvar(produtoDTO)).thenReturn(produtoDTO);
//
//        mockMvc.perform(post("/produtos")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(produtoDTO))
//                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(produtoDTO.getId().toString()))
//                .andExpect(jsonPath("$.nome").value(produtoDTO.getNome()));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminAtualizarEstoque() throws Exception {
//        UUID produtoId = UUID.randomUUID();
//        Integer quantidadeAlteracao = 10;
//
//        mockMvc.perform(patch("/produtos/estoque")
//                        .param("produtoId", produtoId.toString())
//                        .param("quantidadeAlteracao", quantidadeAlteracao.toString())
//                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
//                .andExpect(status().isNoContent());
//
//        verify(produtoUseCase, times(1)).atualizarEstoque(produtoId, quantidadeAlteracao);
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminRemoverProduto() throws Exception {
//        UUID produtoId = UUID.randomUUID();
//
//        mockMvc.perform(delete("/produtos")
//                        .param("id", produtoId.toString())
//                        .with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
//                .andExpect(status().isNoContent());
//
//        verify(produtoUseCase, times(1)).removerProduto(produtoId);
//    }
//}
