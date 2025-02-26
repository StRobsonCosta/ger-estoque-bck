package com.kavex.xtoke.controle_estoque.application.port.in;

import java.util.UUID;

public interface NotaFiscalUseCase {

    void gerarNotaFiscal(UUID vendaId);
}
