package com.kavex.xtoke.controle_estoque.web;

import com.kavex.xtoke.controle_estoque.application.port.in.ClienteUseCase;
import com.kavex.xtoke.controle_estoque.web.dto.ClienteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @GetMapping
    public ResponseEntity<?> buscarPorId(@RequestParam UUID clienteId) {
        return ResponseEntity.ok(clienteUseCase.buscarPorId(clienteId));
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {
        return ResponseEntity.ok(clienteUseCase.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid ClienteDTO clienteDTO) {
        ClienteDTO clienteSalvo = clienteUseCase.salvar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @PutMapping
    public ResponseEntity<?> atualizar(@RequestParam UUID clienteId, @RequestBody @Valid ClienteDTO clienteDTO) {
        ClienteDTO clienteAtualizado = clienteUseCase.atualizar(clienteId, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestParam UUID clienteId) {
        clienteUseCase.excluir(clienteId);
        return ResponseEntity.noContent().build();
    }
}
