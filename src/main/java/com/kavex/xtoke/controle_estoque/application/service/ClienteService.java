package com.kavex.xtoke.controle_estoque.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

//    private final ClienteRepositoryPort clienteRepository;
//    private final ClienteMapper clienteMapper;
//    private final VendaRepositoryPort vendaRepository;
//
//    @Transactional
//    public ClienteDTO salvar(ClienteDTO clienteDTO) {
//        if (clienteRepository.existsByCpf(clienteDTO.getCpf())) {
//            throw new ClienteException("CPF já cadastrado!");
//        }
//        Cliente cliente = clienteMapper.toEntity(clienteDTO);
//        return clienteMapper.toDTO(clienteRepository.save(cliente));
//    }
//
//    @Transactional
//    public void excluir(UUID clienteId) {
//        if (vendaRepository.existsByClienteId(clienteId)) {
//            throw new ClienteException("Não é possível excluir um cliente com vendas associadas.");
//        }
//        clienteRepository.deleteById(clienteId);
//    }
}
