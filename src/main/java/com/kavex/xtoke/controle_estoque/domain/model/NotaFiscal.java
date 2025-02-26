package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nota_fiscal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID vendaId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataNota = LocalDateTime.now();
}
