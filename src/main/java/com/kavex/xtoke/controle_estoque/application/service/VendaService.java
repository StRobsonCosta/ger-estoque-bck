package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.VendaUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.VendaRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.StatusVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventVendaRealizada;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendaService implements VendaUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final VendaRepositoryPort vendaRepository;
    private final ProdutoService produtoService;
    private final VendaMapper vendaMapper;

    @Transactional
    @Override
    @CacheEvict(value = "vendas", key = "#result.id") // Remove do cache após criação
    public VendaDTO criarVenda(VendaDTO vendaDTO) {
        Venda venda = vendaMapper.toEntity(vendaDTO);

        venda.getItens().forEach(item -> {
            produtoService.atualizarEstoque(item.getProduto().getId(), -item.getQuantidade());
        });

        venda.setStatus(StatusVenda.PENDENTE);
        venda = vendaRepository.save(venda);

        eventPublisher.publishEvent(new EventVendaRealizada(venda.getId(), venda.getCliente().getId()));

        return vendaMapper.toDTO(venda);
    }

    @Transactional
    @Override
    @CacheEvict(value = "vendas", key = "#vendaId") // Remove do cache após cancelamento
    public void cancelarVenda(UUID vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.VENDA_NAO_ENCONTRADA));

        if (venda.getStatus() != StatusVenda.PENDENTE)
            throw new BadRequestException(ErroMensagem.VENDA_NAO_PENDENTE);

        venda.getItens().forEach(item -> {
            produtoService.atualizarEstoque(item.getProduto().getId(), item.getQuantidade());
        });

        venda.setStatus(StatusVenda.CANCELADA);
        vendaRepository.save(venda);
    }

    @Transactional
    @Override
    @CachePut(value = "vendas", key = "#vendaId") // Atualiza o cache após alteração
    public VendaDTO atualizarVenda(UUID vendaId, VendaDTO vendaDTO) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.VENDA_NAO_ENCONTRADA));

        if (venda.getStatus() != StatusVenda.PENDENTE)
            throw new BadRequestException(ErroMensagem.VENDA_NAO_PENDENTE);

        vendaMapper.updateFromDTO(vendaDTO, venda);
        vendaRepository.save(venda);

        return vendaMapper.toDTO(venda);
    }

    @Override
    @Cacheable(value = "vendas", key = "#vendaId", unless = "#result == null") // Evita cache de exceções
    public VendaDTO buscarPorId(UUID vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.VENDA_NAO_ENCONTRADA));
        return vendaMapper.toDTO(venda);
    }

    @Override
    @Cacheable(value = "vendas", key = "'all'", unless = "#result.isEmpty()") // Cache para listagem
    public List<VendaDTO> listarVendas() {
        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

}
