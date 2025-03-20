package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.in.NotaFiscalUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.NotaFiscalRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.exception.BadRequestException;
import com.kavex.xtoke.controle_estoque.domain.exception.ErroMensagem;
import com.kavex.xtoke.controle_estoque.domain.model.NotaFiscal;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventNotaFiscalGerada;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotaFiscalService implements NotaFiscalUseCase {

    private final NotaFiscalRepositoryPort notaFiscalRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void gerarNotaFiscal(UUID vendaId) {
        log.info("Iniciando geração da nota fiscal para a venda: {}", vendaId);

        if (notaFiscalRepository.existsByVendaId(vendaId)) {
            log.warn("Nota fiscal já existe para a venda: {}", vendaId);
            throw new BadRequestException(ErroMensagem.NOTA_FISCAL_DUPLICADA);
        }

        try {
            NotaFiscal notaFiscal = new NotaFiscal(null, vendaId, LocalDateTime.now());
            NotaFiscal salva = notaFiscalRepository.save(notaFiscal);

            log.info("Nota fiscal gerada com sucesso. ID: {}, Venda: {}", salva.getId(), vendaId);

            eventPublisher.publishEvent(new EventNotaFiscalGerada(vendaId));
            log.info("Evento de nota fiscal gerada publicado para a venda: {}", vendaId);

//            return salva;
        } catch (Exception e) {
            log.error("Erro ao gerar nota fiscal para a venda: {}", vendaId, e);
            throw new RuntimeException("Erro ao gerar nota fiscal.");
        }
    }
}
