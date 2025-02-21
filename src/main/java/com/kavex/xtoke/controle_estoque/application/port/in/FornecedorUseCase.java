package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;

import java.util.UUID;

public interface FornecedorUseCase {

    FornecedorDTO salvar(FornecedorDTO fornecedorDTO);

    void excluir(UUID fornecedorId);
}
