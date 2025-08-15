package com.kavex.xtoke.controle_estoque.service;

import com.kavex.xtoke.controle_estoque.application.mapper.FornecedorMapper;
import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.service.FornecedorService;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepositoryPort fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @Mock
    private ProdutoRepositoryPort produtoRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    @Test
    void deveSalvarFornecedorComSucesso() {
        FornecedorDTO dto = new FornecedorDTO();
        dto.setCnpj("12345678000199");

        Fornecedor entity = new Fornecedor();
        Fornecedor savedEntity = new Fornecedor();
        savedEntity.setId(UUID.randomUUID());

        FornecedorDTO savedDto = new FornecedorDTO();
        savedDto.setId(savedEntity.getId());

        when(fornecedorRepository.existsByCnpj(dto.getCnpj())).thenReturn(false);
        when(fornecedorMapper.toEntity(dto)).thenReturn(entity);
        when(fornecedorRepository.save(entity)).thenReturn(savedEntity);
        when(fornecedorMapper.toDTO(savedEntity)).thenReturn(savedDto);

        FornecedorDTO result = fornecedorService.salvar(dto);

        assertEquals(savedEntity.getId(), result.getId());
        verify(fornecedorRepository).save(entity);
    }

    @Test
    void deveLancarExcecaoAoSalvarFornecedorComCnpjDuplicado() {
        FornecedorDTO dto = new FornecedorDTO();
        dto.setCnpj("12345678000199");

        when(fornecedorRepository.existsByCnpj(dto.getCnpj())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> fornecedorService.salvar(dto));
        verify(fornecedorRepository, never()).save(any());
    }

    @Test
    void deveBuscarFornecedorPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Fornecedor entity = new Fornecedor();
        entity.setId(id);

        FornecedorDTO dto = new FornecedorDTO();
        dto.setId(id);

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(entity));
        when(fornecedorMapper.toDTO(entity)).thenReturn(dto);

        FornecedorDTO result = fornecedorService.buscarPorId(id);

        assertEquals(id, result.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarFornecedorInexistente() {
        UUID id = UUID.randomUUID();

        when(fornecedorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fornecedorService.buscarPorId(id));
    }

    @Test
    void deveListarTodosOsFornecedores() {
        Fornecedor fornecedor1 = new Fornecedor();
        Fornecedor fornecedor2 = new Fornecedor();

        FornecedorDTO dto1 = new FornecedorDTO();
        FornecedorDTO dto2 = new FornecedorDTO();

        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor1, fornecedor2));
        when(fornecedorMapper.toDTO(fornecedor1)).thenReturn(dto1);
        when(fornecedorMapper.toDTO(fornecedor2)).thenReturn(dto2);

        List<FornecedorDTO> result = fornecedorService.listarTodos();

        assertEquals(2, result.size());
    }

    @Test
    void deveAtualizarFornecedorComSucesso() {
        UUID id = UUID.randomUUID();

        FornecedorDTO dto = new FornecedorDTO();
        dto.setId(id);

        Fornecedor entity = new Fornecedor();
        entity.setId(id);

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(entity));
        when(fornecedorRepository.save(entity)).thenReturn(entity);
        when(fornecedorMapper.toDTO(entity)).thenReturn(dto);

        FornecedorDTO result = fornecedorService.atualizar(id, dto);

        assertEquals(id, result.getId());
        verify(fornecedorMapper).updateFromDTO(dto, entity);
        verify(fornecedorRepository).save(entity);
    }

    @Test
    void deveLancarExcecaoAoAtualizarFornecedorInexistente() {
        UUID id = UUID.randomUUID();
        FornecedorDTO dto = new FornecedorDTO();

        when(fornecedorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fornecedorService.atualizar(id, dto));
    }

    @Test
    void deveExcluirFornecedorComSucesso() {
        UUID id = UUID.randomUUID();

        when(produtoRepository.existsByFornecedorId(id)).thenReturn(false);

        fornecedorService.excluir(id);

        verify(fornecedorRepository).deleteById(id);
    }

    @Test
    void deveLancarExcecaoAoExcluirFornecedorComProdutosVinculados() {
        UUID id = UUID.randomUUID();

        when(produtoRepository.existsByFornecedorId(id)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> fornecedorService.excluir(id));
        verify(fornecedorRepository, never()).deleteById(id);
    }
}
