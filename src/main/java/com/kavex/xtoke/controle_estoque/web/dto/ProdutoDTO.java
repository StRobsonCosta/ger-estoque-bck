package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProdutoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Integer estoque;
    private BigDecimal preco;
    private Long fornecedorId;

}
