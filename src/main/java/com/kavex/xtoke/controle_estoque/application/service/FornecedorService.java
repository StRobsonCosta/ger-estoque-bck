package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.FornecedorMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.FornecedorUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.FornecedorRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FornecedorService implements FornecedorUseCase {

    private final FornecedorRepositoryPort fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final ProdutoRepositoryPort produtoRepository;

    @Transactional
    @Override
    public FornecedorDTO salvar(FornecedorDTO fornecedorDTO) {
        if (fornecedorRepository.existsByCnpj(fornecedorDTO.getCnpj())) {
            throw new BadRequestException(ErroMensagem.CPF_CNPJ_DUPLICADO);
        }
        Fornecedor fornecedor = fornecedorMapper.toEntity(fornecedorDTO);
        return fornecedorMapper.toDTO(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    @Override
    public void excluir(UUID fornecedorId) {
        if (produtoRepository.existsByFornecedorId(fornecedorId)) {
            throw new BadRequestException(ErroMensagem.EXCLUSAO_FORNECEDOR_NEGADA);
        }
        fornecedorRepository.deleteById(fornecedorId);
    }
}
