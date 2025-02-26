package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ProdutoMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.ForbiddenException;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventEstoqueBaixo;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService implements ProdutoUseCase {

    private final ProdutoRepositoryPort produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate; // Para garantir que o evento só dispare após commit

    @Transactional
    @Override
    @Cacheable(value = "produtos", key = "#id")
    public void atualizarEstoque(UUID id, Integer quantidadeAlteracao) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));

        Integer estoqueAtual = produto.getEstoque();
        produto.atualizarEstoque(quantidadeAlteracao);

        produtoRepository.save(produto);

        // Disparar evento APÓS a transação ser confirmada
        transactionTemplate.executeWithoutResult(status -> {
            if (produto.estoqueEstaBaixo())
                eventPublisher.publishEvent(new EventEstoqueBaixo(produto.getId(), estoqueAtual));
        });
    }

    @Override
    @Cacheable(value = "produtos", key = "#id")
    public ProdutoDTO buscarPorId(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));
        return produtoMapper.toDTO(produto);
    }

    @Transactional
    @Override
    @CachePut(value = "produtos", key = "#produtoDTO.id")
    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        // Verifica se já existe um produto com o mesmo nome/código
        if (produtoRepository.existsByNome(produtoDTO.getNome()))
            throw new BadRequestException(ErroMensagem.PRODUTO_DUPLICADO);

        Produto produto = produtoMapper.toEntity(produtoDTO);
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toDTO(salvo);
    }

    @Override
    public List<ProdutoDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public void removerProduto(UUID id) {
        if (!produtoRepository.existsById(id))
            throw new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);

        produtoRepository.deleteById(id);
    }
}