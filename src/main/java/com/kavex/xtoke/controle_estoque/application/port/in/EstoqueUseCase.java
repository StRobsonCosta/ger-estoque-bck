package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.domain.model.Venda;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;

public interface EstoqueUseCase {

    void validarDisponibilidadeEstoque(VendaDTO vendaDTO);

    void atualizarEstoque(Venda venda);

    public void atualizarEstoque(Venda venda, VendaDTO vendaDTO);

}
