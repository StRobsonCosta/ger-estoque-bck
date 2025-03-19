package com.kavex.xtoke.controle_estoque.web.dto;

import com.kavex.xtoke.controle_estoque.domain.model.MetodoPagamento;
import com.kavex.xtoke.controle_estoque.domain.model.StatusVenda;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class VendaDTO {

    private UUID id;
    private UUID clienteId;
    private List<ItemVendaDTO> itens;
    private StatusVenda status;
    private BigDecimal total;
    private MetodoPagamento metodoPagamento;

}
