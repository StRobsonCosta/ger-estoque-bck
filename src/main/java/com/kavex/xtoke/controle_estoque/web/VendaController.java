package com.kavex.xtoke.controle_estoque.web;

import com.kavex.xtoke.controle_estoque.application.port.in.VendaUseCase;
import com.kavex.xtoke.controle_estoque.web.dto.VendaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaUseCase vendaUseCase;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<VendaDTO> buscarVendaPorId(@RequestParam UUID vendaId) {
        VendaDTO vendaDTO = vendaUseCase.buscarPorId(vendaId);
        return ResponseEntity.ok(vendaDTO);
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<VendaDTO>> listarVendas() {
        List<VendaDTO> vendas = vendaUseCase.listarVendas();
        return ResponseEntity.ok(vendas);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarVenda(@RequestBody @Valid VendaDTO vendaDTO) {
        VendaDTO novaVenda = vendaUseCase.criarVenda(vendaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaVenda);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendaDTO> atualizarVenda(@RequestParam UUID vendaId, @RequestBody @Valid VendaDTO vendaDTO) {
        VendaDTO vendaAtualizada = vendaUseCase.atualizarVenda(vendaId, vendaDTO);
        return ResponseEntity.ok(vendaAtualizada);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelarVenda(@RequestParam UUID vendaId) {
        vendaUseCase.cancelarVenda(vendaId);
        return ResponseEntity.noContent().build();
    }

}
