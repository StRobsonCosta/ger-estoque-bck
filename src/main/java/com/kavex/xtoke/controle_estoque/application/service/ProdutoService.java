package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ProdutoMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.ForbiddenException;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventEstoqueBaixo;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence.ProdutoRepositoryAdapter;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService implements ProdutoUseCase {

    private final ProdutoRepositoryPort produtoRepository;
    private final ProdutoRepositoryAdapter produtoAdapter;
    private final ProdutoMapper produtoMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate; // Para garantir que o evento só dispare após commit

    @Transactional
    @Override
    @CachePut(value = "estoque", key = "#mapEstoque")
    public void atualizarEstoques(Map<UUID, Integer> mapEstoque) {
        // Buscar todos os produtos em uma única consulta
        List<Produto> produtos = produtoAdapter.findAllById(mapEstoque.keySet());

        if (produtos.isEmpty())
            throw new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);

        produtos.forEach(produto -> {
            Integer quantidadeAlteracao = mapEstoque.getOrDefault(produto.getId(), 0);
            Integer estoqueAtual = produto.getEstoque();

            produto.atualizarEstoque(quantidadeAlteracao);

            // 🔥 Se o estoque estiver baixo, publica o evento
            dispararEventoEstoqueBaixo(produto, estoqueAtual);
        });

        // Salva todos os produtos de uma vez só, reduzindo as queries
        produtoAdapter.saveAll(produtos);
    }


    @Transactional
    @Override
    @CachePut(value = "estoque", key = "#produtoId")
    public void atualizarEstoque(UUID produtoId, Integer quantidadeAlteracao) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));

        Integer estoqueAtual = produto.getEstoque();
        produto.atualizarEstoque(quantidadeAlteracao);

        produtoRepository.save(produto);

        // 🔥 Se o estoque estiver baixo, publica o evento
        dispararEventoEstoqueBaixo(produto, estoqueAtual);
    }

    private void dispararEventoEstoqueBaixo(Produto produto, Integer estoqueAtual) {
        if (produto.estoqueEstaBaixo()) {}
//            eventPublisher.publishEvent(new EventEstoqueBaixo(produto.getNome(), produto.getId(), estoqueAtual));
    }

    public Integer consultarEstoque(UUID produtoId) {
        return produtoRepository.findEstoqueById(produtoId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));
    }

    @Override
    public Map<UUID, Integer> consultarEstoques(List<UUID> produtoIds) {
        List<Object[]> resultados = produtoRepository.findEstoquesByIds(produtoIds);

        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> (UUID) resultado[0],  // Chave: ID do produto
                        resultado -> (Integer) resultado[1] // Valor: Quantidade em estoque
                ));
    }


    @Override
    @Cacheable(value = "produtos", key = "#id", unless = "#result == null")
    public ProdutoDTO buscarPorId(UUID id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));
    }

    @Transactional
    @Override
    @CachePut(value = "produtos", key = "#result.id")
    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        try {
            Produto produto = produtoMapper.toEntity(produtoDTO);
            Produto salvo = produtoRepository.save(produto);
            return produtoMapper.toDTO(salvo);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(ErroMensagem.PRODUTO_DUPLICADO);
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public ProdutoDTO atualizar(UUID id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO));

        produtoMapper.updateFromDTO(produtoDTO, produto);
        produtoRepository.save(produto);
        return produtoMapper.toDTO(produto);
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