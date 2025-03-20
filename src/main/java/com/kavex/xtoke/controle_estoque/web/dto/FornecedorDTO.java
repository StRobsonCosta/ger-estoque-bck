package com.kavex.xtoke.controle_estoque.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FornecedorDTO {

    private UUID id;
    private String nome;

    @Size(max = 18)
    private String cnpj;
    private String email;
    private String fone;
    private String endereco;

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos
    }

}
