package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.mapper.ClienteMapper;
import com.kavex.xtoke.controle_estoque.application.port.in.ClienteUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.ClienteRepositoryPort;
import com.kavex.xtoke.controle_estoque.application.port.out.VendaRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.NotFoundException;
import com.kavex.xtoke.controle_estoque.domain.model.Cliente;
import com.kavex.xtoke.controle_estoque.domain.model.Fornecedor;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByCpf(clienteDTO.getCpf()))
            throw new BadRequestException(ErroMensagem.CPF_CNPJ_DUPLICADO);

        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Override
    @Cacheable(value = "clientes", key = "#clienteId")
    public ClienteDTO buscarPorId(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.CLIENTE_NAO_ENCONTRADO));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    @CacheEvict(value = "clientes", key = "#clienteId")
    public ClienteDTO atualizar(UUID clienteId, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException(ErroMensagem.CLIENTE_NAO_ENCONTRADO));

        clienteMapper.updateFromDTO(clienteDTO, cliente);
        clienteRepository.save(cliente);
        return clienteMapper.toDTO(cliente);
    }

    @Transactional
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public void excluir(UUID clienteId) {
        if (!clienteRepository.existsById(clienteId))
            throw new BadRequestException(ErroMensagem.CLIENTE_NAO_ENCONTRADO);

        clienteRepository.deleteById(clienteId);
    }
}
