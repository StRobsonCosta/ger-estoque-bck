package com.kavex.xtoke.controle_estoque.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String fone;
    private String endereco;

}
