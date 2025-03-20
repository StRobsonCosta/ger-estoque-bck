package com.kavex.xtoke.controle_estoque.web;

import com.kavex.xtoke.controle_estoque.application.port.in.ClienteUseCase;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @GetMapping
    public ResponseEntity<?> buscarPorId(@RequestParam UUID clienteId) {
        log.info("Buscando cliente pelo ID: {}", clienteId);
        try {
            ClienteDTO cliente = clienteUseCase.buscarPorId(clienteId);
            log.info("Cliente encontrado: {}", cliente);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            log.error("Erro ao buscar cliente pelo ID: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar cliente.");
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {
        log.info("Listando todos os clientes");
        List<ClienteDTO> clientes = clienteUseCase.listarTodos();
        log.info("Total de clientes encontrados: {}", clientes.size());
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid ClienteDTO clienteDTO) {
        log.info("Salvando novo cliente: {}", clienteDTO);
        try {
            ClienteDTO clienteSalvo = clienteUseCase.salvar(clienteDTO);
            log.info("Cliente salvo com sucesso: {}", clienteSalvo);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (Exception e) {
            log.error("Erro ao salvar cliente: {}", clienteDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar cliente.");
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizar(@RequestParam UUID clienteId, @RequestBody @Valid ClienteDTO clienteDTO) {
        log.info("Atualizando cliente ID: {}, Dados: {}", clienteId, clienteDTO);
        try {
            ClienteDTO clienteAtualizado = clienteUseCase.atualizar(clienteId, clienteDTO);
            log.info("Cliente atualizado com sucesso: {}", clienteAtualizado);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar cliente ID: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar cliente.");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestParam UUID clienteId) {
        log.warn("Excluindo cliente ID: {}", clienteId);
        try {
            clienteUseCase.excluir(clienteId);
            log.info("Cliente ID {} excluído com sucesso", clienteId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir cliente ID: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir cliente.");
        }
    }
}

