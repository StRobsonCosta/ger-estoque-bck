package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemVenda {

    private UUID id;
    private Produto produto;
    private Integer quantidade;
    private BigDecimal precoUnit;
    private BigDecimal subtotal;

}
