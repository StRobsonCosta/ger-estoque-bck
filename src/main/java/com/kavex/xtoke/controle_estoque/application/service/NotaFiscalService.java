package com.kavex.xtoke.controle_estoque.application.service;

import com.kavex.xtoke.controle_estoque.application.port.in.NotaFiscalUseCase;
import com.kavex.xtoke.controle_estoque.application.port.out.NotaFiscalRepositoryPort;
import com.kavex.xtoke.controle_estoque.domain.model.NotaFiscal;
import com.kavex.xtoke.controle_estoque.infrastructure.adapter.messaging.EventNotaFiscalGerada;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaFiscalService implements NotaFiscalUseCase {

    private final NotaFiscalRepositoryPort notaFiscalRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void gerarNotaFiscal(UUID vendaId) {
        NotaFiscal notaFiscal = new NotaFiscal(null, vendaId, LocalDateTime.now());
        notaFiscalRepository.save(notaFiscal);

        eventPublisher.publishEvent(new EventNotaFiscalGerada(vendaId));
        System.out.println("📄 Nota fiscal gerada para a venda: " + vendaId);
    }
}
