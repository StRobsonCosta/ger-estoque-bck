package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendaService implements VendaUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final VendaRepositoryPort vendaRepository;
    private final ProdutoUseCase produtoService;
    private final EstoqueService estoqueService;
    private final VendaMapper vendaMapper;

    @Transactional
    @Override
    @CacheEvict(value = "vendas", key = "#result.id") // Remove do cache após criação
    public VendaDTO criarVenda(VendaDTO vendaDTO) {
        log.info("Iniciando criação da venda");

        validarVenda(vendaDTO);

        estoqueService.validarDisponibilidadeEstoque(vendaDTO);

        final Venda venda = vendaMapper.toEntity(vendaDTO);
        venda.definirStatusComBaseNoPagamento();

        estoqueService.atualizarEstoque(venda); //colocado antes do save devido erro  Cannot read field "intCompact" because "augend" is null (subtotal null)

        Venda vendaSalva = vendaRepository.save(venda);
        log.info("Venda criada com sucesso, ID: {}", vendaSalva.getId());

        publicarEventoVenda(vendaSalva);

        return vendaMapper.toDTO(vendaSalva);
    }

    @Transactional
    @Override
    @CacheEvict(value = "vendas", key = "#vendaId") // Remove do cache após cancelamento
    public void cancelarVenda(UUID vendaId) {
        log.info("Cancelando venda ID: {}", vendaId);

        Venda venda = buscarVendaPeloId(vendaId);

        if (venda.getStatus() != StatusVenda.PENDENTE) {
            log.warn("Tentativa de cancelamento de venda não pendente, ID: {}", vendaId);
            throw new BadRequestException(ErroMensagem.VENDA_NAO_PENDENTE);
        }

        venda.getItens().forEach(item -> {
            produtoService.atualizarEstoque(item.getProduto().getId(), item.getQuantidade()); //usar batch
        });

        venda.setStatus(StatusVenda.CANCELADA);
        vendaRepository.save(venda);

        log.info("Venda ID: {} cancelada com sucesso", vendaId);
    }

    @Transactional
    @Override
    @CachePut(value = "vendas", key = "#vendaId") // Atualiza o cache após alteração
    public VendaDTO atualizarVenda(UUID vendaId, VendaDTO vendaDTO) {
        log.info("Atualizando venda ID: {}", vendaId);

        Venda venda = buscarVendaPeloId(vendaId);

        if (venda.getStatus() != StatusVenda.PENDENTE) {
            log.warn("Tentativa de atualização de venda não pendente, ID: {}", vendaId);
            throw new BadRequestException(ErroMensagem.VENDA_NAO_PENDENTE);
        }

        estoqueService.validarDisponibilidadeEstoque(vendaDTO);
        estoqueService.atualizarEstoque(venda, vendaDTO);

        vendaMapper.updateFromDTO(vendaDTO, venda);
        venda.calcularTotal();

        vendaRepository.save(venda);
        //Evento de Atualizar Venda

        log.info("Venda ID: {} atualizada com sucesso", vendaId);
        return vendaMapper.toDTO(venda);
    }

    @Override
    @Cacheable(value = "vendas", key = "#vendaId", unless = "#result == null") // Evita cache de exceções
    public VendaDTO buscarPorId(UUID vendaId) {
        log.info("Buscando venda por ID: {}", vendaId);

        return vendaMapper.toDTO(buscarVendaPeloId(vendaId));
    }

    @Override
//    @Cacheable(value = "vendas", key = "'all'", unless = "#result.isEmpty()") // Cache para listagem
    public List<VendaDTO> listarVendas() {
        log.info("Listando todas as vendas");

        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    private Venda buscarVendaPeloId(UUID vendaId) {
        return vendaRepository.findById(vendaId)
                .orElseThrow(() -> {
                    log.error("Venda não encontrada, ID: {}", vendaId);
                    return new NotFoundException(ErroMensagem.VENDA_NAO_ENCONTRADA);
                });
    }

    private void validarVenda(VendaDTO vendaDTO) {
        if (Objects.isNull(vendaDTO) || Objects.isNull(vendaDTO.getItens()) || vendaDTO.getItens().isEmpty()) {
            log.warn("Venda inválida: itens ausentes ou nulos");
            throw new IllegalArgumentException(ErroMensagem.VENDA_NAO_PERMITIDA.getMensagem());
        }
    }

    private void publicarEventoVenda(Venda venda) {
        if (Objects.nonNull(venda.getCliente()) && Objects.nonNull(venda.getCliente().getId())) {
            log.info("Publicando evento de venda ID: {}", venda.getId());
            eventPublisher.publishEvent(new EventVendaRealizada(venda.getId(), venda.getCliente().getId()));
        }
    }

}
