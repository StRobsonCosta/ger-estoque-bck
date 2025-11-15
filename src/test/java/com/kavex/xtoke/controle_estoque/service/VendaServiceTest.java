package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.VendaRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.service.EstoqueService;
import com.kavex.xtoke.controle_estoque.application.service.VendaService;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.*;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VendaServiceTest {

    @InjectMocks
    private VendaService vendaService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private VendaRepositoryPort vendaRepository;

    @Mock
    private ProdutoUseCase produtoService;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private VendaMapper vendaMapper;


    @Test
    void criarVenda_DeveCriarVendaComSucesso() {
        VendaDTO dto = new VendaDTO();
        dto.setItens(List.of(mock(ItemVendaDTO.class)));
        dto.setMetodoPagamento(MetodoPagamento.DINHEIRO);

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setMetodoPagamento(MetodoPagamento.DINHEIRO);

        when(vendaMapper.toEntity(dto)).thenReturn(venda);
        when(vendaRepository.save(any())).thenReturn(venda);
        when(vendaMapper.toDTO(any())).thenReturn(dto);

        VendaDTO result = vendaService.criarVenda(dto);

        assertNotNull(result);
        verify(estoqueService).validarDisponibilidadeEstoque(dto);
        verify(estoqueService).atualizarEstoque(venda);
    }

    @Test
    void criarVenda_DeveLancarExcecaoQuandoItensForemInvalidos() {
        VendaDTO dto = new VendaDTO();
        dto.setItens(null);

        assertThrows(IllegalArgumentException.class, () -> vendaService.criarVenda(dto));
    }

    @Test
    void cancelarVenda_DeveCancelarVendaPendete() {
        UUID vendaId = UUID.randomUUID();
        Venda venda = new Venda();
        venda.setStatus(StatusVenda.PENDENTE);
        venda.setItens(List.of());

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

        vendaService.cancelarVenda(vendaId);

        assertEquals(StatusVenda.CANCELADA, venda.getStatus());
        verify(vendaRepository).save(venda);
    }

    @Test
    void cancelarVenda_DeveLancarExcecaoSeStatusNaoForPendente() {
        UUID vendaId = UUID.randomUUID();
        Venda venda = new Venda();
        venda.setStatus(StatusVenda.CONCLUIDA);

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

        assertThrows(BadRequestException.class, () -> vendaService.cancelarVenda(vendaId));
    }

    @Test
    void atualizarVenda_DeveAtualizarComSucesso() {
        UUID vendaId = UUID.randomUUID();
        VendaDTO dto = new VendaDTO();
        dto.setItens(List.of(mock(ItemVendaDTO.class)));

        Venda venda = new Venda();
        venda.setStatus(StatusVenda.PENDENTE);

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

        VendaDTO result = new VendaDTO();
        when(vendaMapper.toDTO(any())).thenReturn(result);

        VendaDTO retorno = vendaService.atualizarVenda(vendaId, dto);

        assertNotNull(retorno);
        verify(estoqueService).validarDisponibilidadeEstoque(dto);
        verify(estoqueService).atualizarEstoque(venda, dto);
        verify(vendaRepository).save(venda);
    }

    @Test
    void buscarPorId_DeveRetornarVenda() {
        UUID id = UUID.randomUUID();
        Venda venda = new Venda();

        when(vendaRepository.findById(id)).thenReturn(Optional.of(venda));

        VendaDTO dto = new VendaDTO();
        when(vendaMapper.toDTO(venda)).thenReturn(dto);

        VendaDTO result = vendaService.buscarPorId(id);

        assertNotNull(result);
    }

    @Test
    void buscarPorId_DeveLancarNotFoundException() {
        UUID id = UUID.randomUUID();
        when(vendaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vendaService.buscarPorId(id));
    }

    @Test
    void listarVendas_DeveRetornarListaDeVendas() {
        List<Venda> vendas = List.of(new Venda());
        when(vendaRepository.findAll()).thenReturn(vendas);
        when(vendaMapper.toDTO(any())).thenReturn(new VendaDTO());

        List<VendaDTO> result = vendaService.listarVendas();

        assertEquals(1, result.size());
    }
}
