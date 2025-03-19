package com.kavex.xtoke.controle_estoque.domain.model;

import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetodoPagamento {
    DINHEIRO("Pagamento em Dinheiro à Vista"),
    PIX("Pagamento no Pix"),
    CREDITO("Pagamento no Cartão de Crédito"),
    DEBITO("Pagamento no Cartão de Débito"),
    VOUCHER("Pagamento em Voucher/Vale-Presente"),
    CHEQUE("Pagamento por Cheque");

    private final String descricao;

    public static MetodoPagamento fromString(String value) {
        for (MetodoPagamento status : MetodoPagamento.values()) {
            if (status.name().equalsIgnoreCase(value))
                return status;
        }
        throw new IllegalArgumentException(ErroMensagem.METODO_PAGAMENTO_INCOMPATIVEL.getMensagem() + value);
    }

}
