package com.kavex.xtoke.controle_estoque.domain.model;

import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.exception.ForbiddenException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Column(nullable = false)
    private String unidadeMedida;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime ultimaAtualizacao = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void atualizarEstoque(Integer quantidadeAlteracao) {
        Integer novoEstoque = this.estoque + quantidadeAlteracao;

        if (novoEstoque < 0)
            throw new ForbiddenException(ErroMensagem.ESTOQUE_INSUFICIENTE);

        this.estoque = novoEstoque;
    }

    public Boolean estoqueEstaBaixo() {
        return this.estoque <= this.estoqueMinimo;
    }
}

