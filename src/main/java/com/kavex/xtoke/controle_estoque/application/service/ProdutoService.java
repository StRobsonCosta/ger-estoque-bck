package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ProdutoMapper;
import com.kavex.xtoke.controle_estoque.application.port.out.ProdutoRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.ProdutoException;
import com.kavex.xtoke.controle_estoque.domain.model.Produto;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventEstoqueBaixo;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepositoryPort produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate; // Para garantir que o evento só dispare após commit

    @Transactional
    public void atualizarEstoque(UUID produtoId, Integer quantidadeAlteracao) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoException(ErroMensagem.PRODUTO_NAO_ENCONTRADO.getMensagem()));

        Integer estoqueAtual = produto.getEstoque();
        produto.atualizarEstoque(quantidadeAlteracao);

        produtoRepository.save(produto);

        // Disparar evento APÓS a transação ser confirmada
        transactionTemplate.executeWithoutResult(status -> {
            if (produto.estoqueEstaBaixo())
                eventPublisher.publishEvent(new EventEstoqueBaixo(produto.getId(), estoqueAtual));
        });
    }

    public ProdutoDTO buscarPorId(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoException(ErroMensagem.PRODUTO_NAO_ENCONTRADO.getMensagem()));
        return produtoMapper.toDTO(produto);
    }

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO produtoDTO) {
        // Verifica se já existe um produto com o mesmo nome/código
        if (produtoRepository.existsByNome(produtoDTO.getNome()))
            throw new ProdutoException(ErroMensagem.PRODUTO_DUPLICADO.getMensagem());

        Produto produto = produtoMapper.toEntity(produtoDTO);
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toDTO(salvo);
    }

    public List<ProdutoDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removerProduto(UUID id) {
        if (!produtoRepository.existsById(id))
            throw new ProdutoException(ErroMensagem.PRODUTO_NAO_ENCONTRADO.getMensagem());

        produtoRepository.deleteById(id);
    }
}
