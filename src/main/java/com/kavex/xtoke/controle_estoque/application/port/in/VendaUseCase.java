package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;

import java.util.List;
import java.util.UUID;

public interface VendaUseCase {

    VendaDTO criarVenda(VendaDTO vendaDTO);

    VendaDTO atualizarVenda(UUID vendaId, VendaDTO vendaDTO);

    void cancelarVenda(UUID vendaId);

    VendaDTO buscarPorId(UUID vendaId);

    List<VendaDTO> listarVendas();
}
