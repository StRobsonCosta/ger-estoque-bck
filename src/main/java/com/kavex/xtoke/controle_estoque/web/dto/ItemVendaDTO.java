package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ItemVendaDTO {

    private UUID produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;

}
