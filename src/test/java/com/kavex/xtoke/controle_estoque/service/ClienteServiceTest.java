package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ClienteMapper;
import com.kavex.xtoke.controle_estoque.application.port.out.ClienteRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.service.ClienteService;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setCpf("12345678901");

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(cliente.getId());
        clienteDTO.setCpf(cliente.getCpf());
    }

    @Test
    void deveSalvarClienteComSucesso() {
        when(clienteRepository.existsByCpf(clienteDTO.getCpf())).thenReturn(false);
        when(clienteMapper.toEntity(clienteDTO)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        ClienteDTO salvo = clienteService.salvar(clienteDTO);

        assertNotNull(salvo);
        assertEquals(clienteDTO.getCpf(), salvo.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoCpfDuplicado() {
        when(clienteRepository.existsByCpf(clienteDTO.getCpf())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> clienteService.salvar(clienteDTO));
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        ClienteDTO resultado = clienteService.buscarPorId(cliente.getId());

        assertNotNull(resultado);
        assertEquals(clienteDTO.getId(), resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoBuscarIdInexistente() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clienteService.buscarPorId(cliente.getId()));
    }

    @Test
    void deveListarTodosClientes() {
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        List<ClienteDTO> clientes = clienteService.listarTodos();

        assertEquals(1, clientes.size());
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        doNothing().when(clienteMapper).updateFromDTO(clienteDTO, cliente);
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        ClienteDTO atualizado = clienteService.atualizar(cliente.getId(), clienteDTO);

        assertNotNull(atualizado);
        assertEquals(clienteDTO.getId(), atualizado.getId());
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clienteService.atualizar(cliente.getId(), clienteDTO));
    }

    @Test
    void deveExcluirClienteComSucesso() {
        doNothing().when(clienteRepository).deleteById(cliente.getId());

        assertDoesNotThrow(() -> clienteService.excluir(cliente.getId()));
        verify(clienteRepository, times(1)).deleteById(cliente.getId());
    }

    @Test
    void deveLancarExcecaoAoExcluirClienteInexistente() {
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(clienteRepository).deleteById(cliente.getId());

        assertThrows(BadRequestException.class, () -> clienteService.excluir(cliente.getId()));
    }
}
