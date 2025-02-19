package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemVendaDTO {

    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;

}
