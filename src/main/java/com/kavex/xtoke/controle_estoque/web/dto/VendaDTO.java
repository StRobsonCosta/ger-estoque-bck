package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VendaDTO {

    private Long id;
    private Long clienteId;
    private List<ItemVendaDTO> itens;
    private String status;

}
