package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.application.port.in.VendaUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.VendaRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.ItemVenda;
import com.kavex.xtoke.controle_estoque.domain.model.MetodoPagamento;
import com.kavex.xtoke.controle_estoque.domain.model.StatusVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventVendaRealizada;
import com.kavex.xtoke.controle_estoque.web.dto.ItemVendaDTO;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService implements VendaUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final VendaRepositoryPort vendaRepository;
    private final ProdutoUseCase produtoService;
    private final VendaMapper vendaMapper;

    @Transactional
    @Override
    @CacheEvict(value = "vendas", key = "#result.id") // Remove do cache após criação
    public VendaDTO criarVenda(VendaDTO vendaDTO) {
        validarVenda(vendaDTO);

        validarDisponibilidadeEstoque(vendaDTO);

        final Venda venda = vendaMapper.toEntity(vendaDTO);

        venda.definirStatusComBaseNoPagamento();

        atualizarEstoque(venda); //colocado antes do save devido erro  Cannot read field "intCompact" because "augend" is null (subtotal null)

        Venda vendaSalva = vendaRepository.save(venda);

        publicarEventoVenda(vendaSalva);

        return vendaMapper.toDTO(vendaSalva);
    }

    private void atualizarEstoque(Venda venda) {
        Map<UUID, Integer> mapAlteracoes = new HashMap<>();

        venda.getItens().forEach(item -> {
            item.calcularSubtotal();
            mapAlteracoes.put(item.getProduto().getId(), -item.getQuantidade());
        });
        produtoService.atualizarEstoques(mapAlteracoes);
    }

    private void atualizarEstoque(Venda venda, VendaDTO vendaDTO) {
        Map<UUID, Integer> mapQuant = vendaDTO.getItens().stream()
                .collect(Collectors.toMap(ItemVendaDTO::getProdutoId, ItemVendaDTO::getQuantidade));

        Map<UUID, Integer> mapAlteracoes = new HashMap<>();

        venda.getItens().forEach(item -> {
            item.calcularSubtotal();

            Integer novaQuant = mapQuant.getOrDefault(item.getProduto().getId(), 0);
            Integer diferencaQuant = novaQuant - item.getQuantidade();

            mapAlteracoes.put(item.getProduto().getId(), -(diferencaQuant));

        });

        produtoService.atualizarEstoques(mapAlteracoes);
    }


    private void validarVenda(VendaDTO vendaDTO) {
        if (Objects.isNull(vendaDTO) || Objects.isNull(vendaDTO.getItens()) || vendaDTO.getItens().isEmpty()) {
            throw new IllegalArgumentException(ErroMensagem.VENDA_NAO_PERMITIDA.getMensagem());
        }
    }

    private void validarDisponibilidadeEstoque(VendaDTO vendaDTO) {
        Map<UUID, Integer> estoques = consultarEstoque(vendaDTO);

        List<String> produtosSemEstoque = vendaDTO.getItens().stream()
                .filter(item -> {
                    Integer estoqueDisponivel = estoques.getOrDefault(item.getProdutoId(), 0);
                    return item.getQuantidade() > estoqueDisponivel; // Verifica se falta estoque
                })
                .map(item -> "Produto ID: " + item.getProdutoId() + " - Estoque disponível: "
                        + estoques.getOrDefault(item.getProdutoId(), 0) + ", Solicitado: " + item.getQuantidade())
                .collect(Collectors.toList());

        if (!produtosSemEstoque.isEmpty()) {
            throw new IllegalArgumentException(ErroMensagem.ESTOQUES_INSUFICIENTE.getMensagem()
                    + String.join("; ", produtosSemEstoque));
        }

        //lançar evento de estoque esgotado
    }

    private Map<UUID, Integer> consultarEstoque(VendaDTO vendaDT) {
        List<UUID> idsProdutos = vendaDT.getItens().stream()
                .map(ItemVendaDTO::getProdutoId)
                .collect(Collectors.toList());

        return produtoService.consultarEstoques(idsProdutos);

    }

    private void publicarEventoVenda(Venda venda) {
        if (Objects.nonNull(venda.getCliente()) && Objects.nonNull(venda.getCliente().getId())) {
            eventPublisher.publishEvent(new EventVendaRealizada(venda.getId(), venda.getCliente().getId()));
        }
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

        validarDisponibilidadeEstoque(vendaDTO);
        atualizarEstoque(venda, vendaDTO);

        vendaMapper.updateFromDTO(vendaDTO, venda);
        vendaRepository.save(venda);
        //Evento de Atualizar Venda
        venda.calcularTotal();

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
