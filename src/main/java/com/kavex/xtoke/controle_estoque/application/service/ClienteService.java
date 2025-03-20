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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        log.info("Tentando salvar cliente com CPF: {}", clienteDTO.getCpf());

        if (clienteRepository.existsByCpf(clienteDTO.getCpf())) {
            log.warn("Tentativa de cadastro com CPF duplicado: {}", clienteDTO.getCpf());
            throw new BadRequestException(ErroMensagem.CPF_CNPJ_DUPLICADO);
        }
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        Cliente salvo = clienteRepository.save(cliente);

        log.info("Cliente salvo com sucesso! ID: {}", salvo.getId());
        return clienteMapper.toDTO(salvo);
    }

    @Override
    @Cacheable(value = "clientes", key = "#clienteId")
    public ClienteDTO buscarPorId(UUID clienteId) {
        log.info("Buscando cliente com ID: {}", clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para o ID: {}", clienteId);
                    return new NotFoundException(ErroMensagem.CLIENTE_NAO_ENCONTRADO);
                });

        log.info("Cliente encontrado: {}", clienteId);
        return clienteMapper.toDTO(cliente);
    }

    @Override
    public List<ClienteDTO> listarTodos() {
        log.info("Listando todos os clientes");

        List<ClienteDTO> clientes = clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Total de clientes encontrados: {}", clientes.size());
        return clientes;
    }

    @Transactional
    @Override
    @CacheEvict(value = "clientes", key = "#clienteId")
    public ClienteDTO atualizar(UUID clienteId, ClienteDTO clienteDTO) {
        log.info("Atualizando cliente com ID: {}", clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para atualização: {}", clienteId);
                    return new NotFoundException(ErroMensagem.CLIENTE_NAO_ENCONTRADO);
                });

        clienteMapper.updateFromDTO(clienteDTO, cliente);
        log.info("Cliente atualizado com sucesso: {}", clienteId);

        return clienteMapper.toDTO(cliente);
    }

    @Transactional
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public void excluir(UUID clienteId) {
        log.info("Excluindo cliente com ID: {}", clienteId);

        try {
            clienteRepository.deleteById(clienteId);
            log.info("Cliente excluído com sucesso: {}", clienteId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Tentativa de exclusão de cliente não encontrado: {}", clienteId);
            throw new BadRequestException(ErroMensagem.CLIENTE_NAO_ENCONTRADO);
        }
    }
}
