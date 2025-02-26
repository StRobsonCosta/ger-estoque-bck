package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class VendaDTO {

    private UUID id;
    private Long clienteId;
    private List<ItemVendaDTO> itens;
    private String status;

}
