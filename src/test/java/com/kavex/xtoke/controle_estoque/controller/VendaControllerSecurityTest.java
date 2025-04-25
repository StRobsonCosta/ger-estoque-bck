//package com.kavex.xtoke.controle_estoque.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kavex.xtoke.controle_estoque.application.port.in.VendaUseCase;
//import com.kavex.xtoke.controle_estoque.domain.model.StatusVenda;
//import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
//import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("test")
//public class VendaControllerSecurityTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @InjectMocks
//    private VendaUseCase vendaUseCase;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private static VendaDTO getVendaDTO() {
//        return VendaDTO.builder()
//                .id(null) // ID será gerado no serviço
//                .clienteId(UUID.randomUUID())
//                .itens(List.of(
//                        ItemVendaDTO.builder()
//                                .produtoId(UUID.randomUUID())
//                                .quantidade(2)
//                                .precoUnitario(BigDecimal.valueOf(100))
//                                .build()
//                ))
//                .status(StatusVenda.PENDENTE)
//                .build();
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminBuscarVendaPorId() throws Exception {
//        UUID vendaId = UUID.randomUUID();
//        VendaDTO vendaDTO = getVendaDTO();
//
//        when(vendaUseCase.buscarPorId(vendaId)).thenReturn(vendaDTO);
//
//        mockMvc.perform(get("/vendas")
//                        .param("vendaId", vendaId.toString())
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(vendaId.toString()));
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    void devePermitirUsuarioListarVendas() throws Exception {
//        List<VendaDTO> vendas = List.of(getVendaDTO());
//        when(vendaUseCase.listarVendas()).thenReturn(vendas);
//
//        mockMvc.perform(get("/api/vendas/listar").with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(vendas.size()));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminCriarVenda() throws Exception {
//        final VendaDTO vendaDTO = getVendaDTO();
//        VendaDTO vendaCriada = getVendaDTO();
//        vendaCriada.setId(UUID.randomUUID());
//
//        when(vendaUseCase.criarVenda(any(VendaDTO.class))).thenReturn(vendaCriada);
//
//        mockMvc.perform(post("/api/vendas")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(vendaDTO))
//                        .with(csrf()))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").isNotEmpty());
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    void deveNegarCriacaoVendaParaUsuarioComum() throws Exception {
//        VendaDTO vendaDTO = getVendaDTO();
//
//        mockMvc.perform(post("/api/vendas")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(vendaDTO))
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminAtualizarVenda() throws Exception {
//        UUID vendaId = UUID.randomUUID();
//        VendaDTO vendaDTO = getVendaDTO();
//        vendaDTO.setId(vendaId);
//        when(vendaUseCase.atualizarVenda(eq(vendaId), any(VendaDTO.class))).thenReturn(vendaDTO);
//
//        mockMvc.perform(put("/api/vendas")
//                        .param("vendaId", vendaId.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(vendaDTO))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.descricao").value("Produto Atualizado"));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void devePermitirAdminCancelarVenda() throws Exception {
//        UUID vendaId = UUID.randomUUID();
//        doNothing().when(vendaUseCase).cancelarVenda(vendaId);
//
//        mockMvc.perform(delete("/api/vendas")
//                        .param("vendaId", vendaId.toString())
//                        .with(csrf()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void deveNegarAcessoParaNaoAutenticado() throws Exception {
//        mockMvc.perform(get("/api/vendas/listar"))
//                .andExpect(status().isUnauthorized());
//    }
//
//}
