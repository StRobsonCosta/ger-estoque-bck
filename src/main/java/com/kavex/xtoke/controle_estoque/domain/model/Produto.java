package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
    private String unidadeMedida;
    private Fornecedor fornecedor;
    private LocalDateTime dataCadastro;
    private LocalDateTime ultimaAtualizacao;

}
