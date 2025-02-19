package com.kavex.xtoke.controle_estoque.application.port.out;

public interface NotificacaoServicePort {
    void enviarNotificacao(String destinatario, String msg);
}
