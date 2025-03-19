package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.FornecedorMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.FornecedorUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FornecedorService implements FornecedorUseCase {

    private final FornecedorRepositoryPort fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final ProdutoRepositoryPort produtoRepository;

    @Transactional
    @Override
    @CacheEvict(value = "fornecedores", allEntries = true)
    public FornecedorDTO salvar(FornecedorDTO fornecedorDTO) {
        if (fornecedorRepository.existsByCnpj(fornecedorDTO.getCnpj()))
            throw new BadRequestException(ErroMensagem.CPF_CNPJ_DUPLICADO);

        Fornecedor fornecedor = fornecedorMapper.toEntity(fornecedorDTO);
        return fornecedorMapper.toDTO(fornecedorRepository.save(fornecedor));
    }

    @Override
    @Cacheable(value = "fornecedores", key = "#fornecedorId")
    public FornecedorDTO buscarPorId(UUID fornecedorId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.FORNECEDOR_NAO_ENCONTRADO));
        return fornecedorMapper.toDTO(fornecedor);
    }

    @Override
    public List<FornecedorDTO> listarTodos() {
        return fornecedorRepository.findAll().stream()
                .map(fornecedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    @CacheEvict(value = "fornecedores", key = "#fornecedorId")
    public FornecedorDTO atualizar(UUID fornecedorId, FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.FORNECEDOR_NAO_ENCONTRADO));

        fornecedorMapper.updateFromDTO(fornecedorDTO, fornecedor);
        fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toDTO(fornecedor);
    }

    @Transactional
    @Override
    @CacheEvict(value = "fornecedores", allEntries = true)
    public void excluir(UUID fornecedorId) {
        if (produtoRepository.existsByFornecedorId(fornecedorId))
            throw new BadRequestException(ErroMensagem.EXCLUSAO_FORNECEDOR_NEGADA);

        fornecedorRepository.deleteById(fornecedorId);
    }
}
