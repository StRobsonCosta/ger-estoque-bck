package com.kavex.xtoke.controle_estoque.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String fone;
    private String endereco;
    private LocalDateTime dataCadastro;

}
