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
        log.info("Tentando salvar fornecedor com CNPJ: {}", fornecedorDTO.getCnpj());

        if (fornecedorRepository.existsByCnpj(fornecedorDTO.getCnpj())) {
            log.warn("Tentativa de cadastro com CNPJ duplicado: {}", fornecedorDTO.getCnpj());
            throw new BadRequestException(ErroMensagem.CPF_CNPJ_DUPLICADO);
        }

        Fornecedor fornecedor = fornecedorMapper.toEntity(fornecedorDTO);
        FornecedorDTO salvo = fornecedorMapper.toDTO(fornecedorRepository.save(fornecedor));

        log.info("Fornecedor salvo com sucesso. ID: {}", salvo.getId());
        return salvo;
    }

    @Override
    @Cacheable(value = "fornecedores", key = "#fornecedorId")
    public FornecedorDTO buscarPorId(UUID fornecedorId) {
        log.info("Buscando fornecedor com ID: {}", fornecedorId);

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> {
                    log.error("Fornecedor não encontrado para ID: {}", fornecedorId);
                    return new NotFoundException(ErroMensagem.FORNECEDOR_NAO_ENCONTRADO);
                });

        log.info("Fornecedor encontrado: ID: {}", fornecedor.getId());
        return fornecedorMapper.toDTO(fornecedor);
    }

    @Override
    public List<FornecedorDTO> listarTodos() {
        log.info("Listando todos os fornecedores...");
        List<FornecedorDTO> fornecedores = fornecedorRepository.findAll().stream()
                .map(fornecedorMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Total de fornecedores encontrados: {}", fornecedores.size());
        return fornecedores;
    }

    @Transactional
    @Override
    @CacheEvict(value = "fornecedores", key = "#fornecedorId")
    public FornecedorDTO atualizar(UUID fornecedorId, FornecedorDTO fornecedorDTO) {
        log.info("Atualizando fornecedor com ID: {}", fornecedorId);

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> {
                    log.error("Fornecedor não encontrado para atualização. ID: {}", fornecedorId);
                    return new NotFoundException(ErroMensagem.FORNECEDOR_NAO_ENCONTRADO);
                });

        fornecedorMapper.updateFromDTO(fornecedorDTO, fornecedor);
        fornecedorRepository.save(fornecedor);

        log.info("Fornecedor atualizado com sucesso. ID: {}", fornecedorId);
        return fornecedorMapper.toDTO(fornecedor);
    }

    @Transactional
    @Override
    @CacheEvict(value = "fornecedores", allEntries = true)
    public void excluir(UUID fornecedorId) {
        log.info("Tentando excluir fornecedor com ID: {}", fornecedorId);

        if (produtoRepository.existsByFornecedorId(fornecedorId)) {
            log.warn("Exclusão negada para fornecedor ID: {}. Existem produtos associados.", fornecedorId);
            throw new BadRequestException(ErroMensagem.EXCLUSAO_FORNECEDOR_NEGADA);
        }

        fornecedorRepository.deleteById(fornecedorId);
        log.info("Fornecedor excluído com sucesso. ID: {}", fornecedorId);
    }
}
