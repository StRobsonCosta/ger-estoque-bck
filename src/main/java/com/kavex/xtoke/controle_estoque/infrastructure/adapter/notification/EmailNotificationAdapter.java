package com.kavex.xtoke.controle_estoque.infrastructure.adapter.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Obtém o e-mail do remetente do application.yml
    private String fromEmail;

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(mensagem, true); // Habilita HTML no corpo do e-mail

            mailSender.send(mimeMessage);
            log.info("📧 E-mail enviado para {}", destinatario);
        } catch (MessagingException | jakarta.mail.MessagingException e) {
            log.error("❌ Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }

}
