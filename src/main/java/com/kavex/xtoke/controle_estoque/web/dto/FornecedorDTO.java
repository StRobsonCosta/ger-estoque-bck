package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FornecedorDTO {

    private UUID id;
    private String nome;
    private String cnpj;
    private String email;
    private String fone;
    private String endereco;

}
