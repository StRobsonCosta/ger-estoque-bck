package com.kavex.xtoke.controle_estoque.web;

import com.kavex.xtoke.controle_estoque.application.port.in.FornecedorUseCase;
import com.kavex.xtoke.controle_estoque.web.dto.FornecedorDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorUseCase fornecedorUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> buscarPorId(@RequestParam UUID id) {
        return ResponseEntity.ok(fornecedorUseCase.buscarPorId(id));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarFornecedores() {
        return ResponseEntity.ok(fornecedorUseCase.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> salvar(@RequestBody @Valid FornecedorDTO fornecedorDTO) {
        FornecedorDTO fornecedorCriado = fornecedorUseCase.salvar(fornecedorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorCriado);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> atualizar(@RequestBody @Valid FornecedorDTO fornecedorDTO, @RequestParam UUID id) {
        FornecedorDTO fornecedorAtualizado = fornecedorUseCase.atualizar(id, fornecedorDTO);
        return ResponseEntity.ok(fornecedorAtualizado);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> excluir(@RequestParam UUID id) {
        fornecedorUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
