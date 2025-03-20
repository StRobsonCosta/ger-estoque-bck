package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ClienteDTO {

    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String fone;
    private String endereco;

    public void setCpf(String cpf) {
        this.cpf = cpf.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos
    }

}
