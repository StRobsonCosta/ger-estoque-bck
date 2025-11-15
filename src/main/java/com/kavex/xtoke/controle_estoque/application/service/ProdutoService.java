package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ProdutoMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.persistence.ProdutoRepositoryAdapter;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Iniciando atualização do estoque em Batch por Map ");

        List<Produto> produtos = produtoAdapter.findAllById(mapEstoque.keySet());

        if (produtos.isEmpty()) {
            log.warn("Produtos não Encontrados: IDs Divergentes ou nulos");
            throw new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
        }


        produtos.forEach(produto -> {
            Integer quantidadeAlteracao = mapEstoque.getOrDefault(produto.getId(), 0);
            Integer estoqueAtual = produto.getEstoque();

            log.info("Atualizando Estoque para ID do Produto: {}", produto.getId());

            produto.atualizarEstoque(quantidadeAlteracao);

            // 🔥 Se o estoque estiver baixo, publica o evento
            dispararEventoEstoqueBaixo(produto, estoqueAtual);
        });

        produtoAdapter.saveAll(produtos);
        log.info("Produtos Atualizados no Banco de Dados");
    }


    @Transactional
    @Override
    @CachePut(value = "estoque", key = "#produtoId")
    public void atualizarEstoque(UUID produtoId, Integer quantidadeAlteracao) {
        log.info("Iniciando atualização do estoque do produto de ID: {}", produtoId);

        final Produto produto = buscarPorId(produtoId);

        Integer estoqueAtual = produto.getEstoque();
        produto.atualizarEstoque(quantidadeAlteracao);

        produtoRepository.save(produto);
        log.info("Produto Atualizado no Banco de Dados, ID: {}", produto.getId());

        // 🔥 Se o estoque estiver baixo, publica o evento
        dispararEventoEstoqueBaixo(produto, estoqueAtual);
    }

    private Produto buscarPorId(UUID produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> {
                    log.warn("Produto não Encontrado para o ID: {}", produtoId);
                    return new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
                });
    }

    private void dispararEventoEstoqueBaixo(Produto produto, Integer estoqueAtual) {
        if (produto.estoqueEstaBaixo()) {
            log.info("Iniciando disparo de Evento de Estoque Baixo para produto de ID: {}", produto.getId());
        }
//            eventPublisher.publishEvent(new EventEstoqueBaixo(produto.getNome(), produto.getId(), estoqueAtual));
    }

    public Integer consultarEstoque(UUID produtoId) {
        return produtoRepository.findEstoqueById(produtoId)
                .orElseThrow(() -> {
                    log.warn("Estoque não Encontrado para o ID de Produto: {}", produtoId);
                    return new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
                });
    }

    @Override
    public Map<UUID, Integer> consultarEstoques(List<UUID> produtoIds) {
        log.info("Listando Estoques Disponíveis para Lista de ID de Produtos ");

        List<Object[]> resultados = produtoRepository.findEstoquesByIds(produtoIds);

        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> (UUID) resultado[0],  // Chave: ID do produto
                        resultado -> (Integer) resultado[1] // Valor: Quantidade em estoque
                ));
    }


    @Override
    @Cacheable(value = "produtos", key = "#id", unless = "#result == null")
    public ProdutoDTO buscarProdutoDtoPorId(UUID id) {
        log.info("Buscando Produto por ID: {}", id);

        return produtoRepository.findById(id)
                .map(produtoMapper::toDTO)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado, ID: {}", id);
                    return new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
                });
    }

    @Transactional
    @Override
    @CachePut(value = "produtos", key = "#result.id")
    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        log.info("Iniciando Salvar Produto");
        try {
            Produto produto = produtoMapper.toEntity(produtoDTO);
            Produto salvo = produtoRepository.save(produto);

            return produtoMapper.toDTO(salvo);
        } catch (DataIntegrityViolationException e) {
            log.error("Erro ao Salvar Produto ", e);
            throw new BadRequestException(ErroMensagem.PRODUTO_DUPLICADO);
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public ProdutoDTO atualizar(UUID id, ProdutoDTO produtoDTO) {
        log.info("Iniciando Atualização de Produto");

        Produto produto = buscarPorId(id);

        produtoMapper.updateFromDTO(produtoDTO, produto);
        produtoRepository.save(produto);
        return produtoMapper.toDTO(produto);
    }

    @Override
    public List<ProdutoDTO> listarProdutos() {
        log.info("Listando Produtos");

        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public void removerProduto(UUID id) {
        log.info("Removendo Produto");

        if (!produtoRepository.existsById(id)) {
            log.warn("Produto Inexistente, ID: {}", id);
            throw new NotFoundException(ErroMensagem.PRODUTO_NAO_ENCONTRADO);
        }

        produtoRepository.deleteById(id);

        log.info("Produto ID: {} Deletado com sucesso", id);
    }
}