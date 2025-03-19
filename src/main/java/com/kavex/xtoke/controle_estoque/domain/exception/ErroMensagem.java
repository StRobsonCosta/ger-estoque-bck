package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErroMensagem {

    // 🔹 Erros 400 - BAD_REQUEST
    ESTOQUE_INSUFICIENTE(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto."),
    ESTOQUES_INSUFICIENTE(HttpStatus.BAD_REQUEST, "Estoque insuficiente para os seguintes produtos: "),
    VENDA_NAO_PERMITIDA(HttpStatus.BAD_REQUEST, "A venda deve conter pelo menos um item."),
    VENDA_NAO_PENDENTE(HttpStatus.BAD_REQUEST, "Apenas vendas pendentes podem ser manipuladas."),
    PRODUTO_DUPLICADO(HttpStatus.BAD_REQUEST, "Ops, Duplicado! Já existe um Produto com esse nome cadastrado."),
    CPF_CNPJ_DUPLICADO(HttpStatus.BAD_REQUEST, "Ops, Duplicado! CPF/CNPJ já cadastrado na Base de Dados."),
    CPF_CNPJ_FORMATO_INVALIDO(HttpStatus.BAD_REQUEST, "Ops, Formato Inválido! CPF/CNPJ possui tamanho diferente de 11/14 caracteres ou caractere inválido."),
    EXCLUSAO_FORNECEDOR_NEGADA(HttpStatus.BAD_REQUEST, "Não é possível excluir um fornecedor com produtos associados."),
    EMAIL_JA_CADASTRADO(HttpStatus.BAD_REQUEST, "Eita! Este email já foi usado em um cadastro aqui."),

    METODO_PAGAMENTO_INCOMPATIVEL(HttpStatus.NOT_ACCEPTABLE, "Poxa! Essa forma de pagamento NÃO É ACEITÁVEL: "),
    STATUS_VENDA_INVALIDO(HttpStatus.NOT_ACCEPTABLE, "Status de venda inválido: "),

    // 🔹 Erros 404 - NOT_FOUND
    PRODUTO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Produto não encontrado para o ID fornecido."),
    VENDA_NAO_ENCONTRADA(HttpStatus.NOT_FOUND, "Venda não encontrada para o ID fornecido."),
    FORNECEDOR_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Fornecedor não encontrado para o ID fornecido."),
    USUARIO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Usuário não encontrado."),
    CLIENTE_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "Cliente não encontrado."),

    // 🔹 Erros 401 - UNAUTHORIZED
    NAO_AUTORIZADO(HttpStatus.UNAUTHORIZED, "Credenciais inválidas ou sessão expirada."),

    // 🔹 Erros 403 - FORBIDDEN
    SEM_PERMISSAO_CANCELAR_VENDA(HttpStatus.FORBIDDEN, "Usuário não tem permissão para cancelar esta venda."),
    SEM_PERMISSAO_MODIFICAR_ESTOQUE(HttpStatus.FORBIDDEN, "Usuário não tem permissão para modificar o estoque."),
    SEM_PERMISSAO_ACESSO_RELATORIOS(HttpStatus.FORBIDDEN, "Acesso negado aos relatórios de vendas."),
    SEM_PERMISSAO_CADASTRO_PRODUTO(HttpStatus.FORBIDDEN, "Usuário não tem permissão para cadastrar produtos."),
    VENDA_FINALIZADA_NAO_MODIFICAVEL(HttpStatus.FORBIDDEN, "Venda finalizada não pode ser modificada."),
    ACESSO_NEGADO(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.");

    private final HttpStatus status;
    private final String mensagem;
}
