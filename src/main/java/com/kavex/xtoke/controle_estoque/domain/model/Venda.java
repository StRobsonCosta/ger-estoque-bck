package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vendas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "venda_id")
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PrePersist
    @PreUpdate
    public void calcularTotal() {
        this.total = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void definirStatusComBaseNoPagamento() {
        this.status = this.metodoPagamento.equals(MetodoPagamento.DINHEIRO)
                ? StatusVenda.CONCLUIDA
                : StatusVenda.PENDENTE;
    }
}

