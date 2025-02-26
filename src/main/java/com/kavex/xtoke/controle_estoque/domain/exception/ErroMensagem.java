package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErroMensagem {

    // 🔹 Erros 400 - BAD_REQUEST
    ESTOQUE_INSUFICIENTE(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto."),
    VENDA_NAO_PERMITIDA(HttpStatus.BAD_REQUEST, "Venda não permitida devido a restrições."),
    VENDA_NAO_PENDENTE(HttpStatus.BAD_REQUEST, "Apenas vendas pendentes podem ser manipuladas."),
    PRODUTO_DUPLICADO(HttpStatus.BAD_REQUEST, "Ops, Duplicado! Já existe um Produto com esse nome cadastrado."),
    CPF_CNPJ_DUPLICADO(HttpStatus.BAD_REQUEST, "Ops, Duplicado! CPF/CNPJ já cadastrado na Base de Dados."),
    EXCLUSAO_FORNECEDOR_NEGADA(HttpStatus.BAD_REQUEST, "Não é possível excluir um fornecedor com produtos associados."),

    // 🔹 Erros 404 - NOT_FOUND
    PRODUTO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Produto não encontrado para o ID fornecido."),
    VENDA_NAO_ENCONTRADA(HttpStatus.NOT_FOUND, "Venda não encontrada para o ID fornecido."),
    FORNECEDOR_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Fornecedor não encontrado para o ID fornecido."),
    USUARIO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Usuário não encontrado."),

    // 🔹 Erros 403 - FORBIDDEN
    SEM_PERMISSAO_CANCELAR_VENDA(HttpStatus.FORBIDDEN, "Usuário não tem permissão para cancelar esta venda."),
    SEM_PERMISSAO_MODIFICAR_ESTOQUE(HttpStatus.FORBIDDEN, "Usuário não tem permissão para modificar o estoque."),
    SEM_PERMISSAO_ACESSO_RELATORIOS(HttpStatus.FORBIDDEN, "Acesso negado aos relatórios de vendas."),
    SEM_PERMISSAO_CADASTRO_PRODUTO(HttpStatus.FORBIDDEN, "Usuário não tem permissão para cadastrar produtos."),
    VENDA_FINALIZADA_NAO_MODIFICAVEL(HttpStatus.FORBIDDEN, "Venda finalizada não pode ser modificada.");

    private final HttpStatus status;
    private final String mensagem;
}
