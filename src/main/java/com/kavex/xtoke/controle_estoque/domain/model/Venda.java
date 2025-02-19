package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venda {

    private UUID id;
    private List<ItemVenda> itens;
    private BigDecimal total;
    private String metodoPagamento;
    private LocalDateTime dataVenda;
    private StatusVenda status;
    private Cliente cliente;
}
