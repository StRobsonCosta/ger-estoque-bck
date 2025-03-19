package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "itens_venda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (Objects.nonNull(precoUnitario) && Objects.nonNull(quantidade))
            this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}

