package com.kavex.xtoke.controle_estoque.application.port.in;

import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;

import java.util.List;
import java.util.UUID;

public interface ClienteUseCase {

    ClienteDTO salvar(ClienteDTO clienteDTO);

    void excluir(UUID id);

    ClienteDTO buscarPorId(UUID id);

    List<ClienteDTO> listarTodos();

    ClienteDTO atualizar(UUID id, ClienteDTO clienteDTO);
}
