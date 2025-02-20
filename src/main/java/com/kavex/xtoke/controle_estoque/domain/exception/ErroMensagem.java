package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErroMensagem {

    ESTOQUE_INSUFICIENTE("Estoque insuficiente para o produto."),
    PRODUTO_NAO_ENCONTRADO("Produto não encontrado para o ID fornecido."),
    VENDA_NAO_PERMITIDA("Venda não permitida devido a restrições."),
    PRODUTO_DUPLICADO("Ops, Duplicado! Já existe um Produto com esse nome cadastrado.");

    private final String mensagem;
}
