package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.VendaMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.VendaUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.VendaRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.StatusVenda;
import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.infrastructure.queue.RedisEventQueueService;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendaService implements VendaUseCase {

    private final RedisEventQueueService redisEventQueueService;
    private final VendaRepositoryPort vendaRepository;
    private final ProdutoService produtoService;
    private final VendaMapper vendaMapper;

    @Transactional
    @Override
    public VendaDTO criarVenda(VendaDTO vendaDTO) {
        Venda venda = vendaMapper.toEntity(vendaDTO);
        venda.getItens().forEach(item -> {
            produtoService.atualizarEstoque(item.getProduto().getId(), -item.getQuantidade());
        });
        venda.setStatus(StatusVenda.PENDENTE);
        venda = vendaRepository.save(venda);

        redisEventQueueService.adicionarEventoNaFila("Venda realizada: " + venda.getId());

        return vendaMapper.toDTO(venda);
    }

    @Transactional
    @Override
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
    public VendaDTO buscarPorId(UUID vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.VENDA_NAO_ENCONTRADA));
        return vendaMapper.toDTO(venda);
    }

    @Override
    public List<VendaDTO> listarVendas() {
        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

}
