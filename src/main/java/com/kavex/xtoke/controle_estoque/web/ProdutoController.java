package com.kavex.xtoke.controle_estoque.web;

import com.kavex.xtoke.controle_estoque.application.port.in.ProdutoUseCase;
import com.kavex.xtoke.controle_estoque.web.dto.ProdutoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoUseCase produtoUseCase;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO> buscarPorId(@RequestParam UUID id) {
        return ResponseEntity.ok(produtoUseCase.buscarPorId(id));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProdutoDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoUseCase.listarProdutos());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO> salvar(@RequestBody @Valid ProdutoDTO produtoDTO) {
        ProdutoDTO produtoCriado = produtoUseCase.salvar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @PutMapping("/estoque")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarEstoque(@RequestParam UUID produtoId, @RequestParam Integer quantidadeAlteracao) {
        produtoUseCase.atualizarEstoque(produtoId, quantidadeAlteracao);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerProduto(@RequestParam UUID id) {
        produtoUseCase.removerProduto(id);
        return ResponseEntity.noContent().build();
    }
}
