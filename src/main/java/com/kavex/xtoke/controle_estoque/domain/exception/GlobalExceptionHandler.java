package com.kavex.xtoke.controle_estoque.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex, WebRequest request) {
        log.warn("Erro capturado: {}", ex.getMensagem());

        return buildErrorResponse(ex.getStatus(), ex.getMensagem(), request);
    }

    /**
     * Captura erros de integridade do banco (ex: chave duplicada, tamanho de campo inválido).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {

        String errorMessage = "Erro de integridade no banco de dados.";

        if (ex.getMessage().contains("value too long for type character varying")) {
            errorMessage = ErroMensagem.CPF_CNPJ_FORMATO_INVALIDO.getMensagem();
        } else if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
            if (ex.getMessage().contains("email")) {
                errorMessage = ErroMensagem.EMAIL_JA_CADASTRADO.getMensagem();
            } else {
                errorMessage = ErroMensagem.CPF_CNPJ_DUPLICADO.getMensagem();
            }
        }

        log.error("Erro de integridade: {}", errorMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    /**
     * Captura erros de validação (ex: campos obrigatórios, formato inválido).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Erro de validação: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, String.join("; ", errors), request);
    }

    /**
     * Captura exceções de autenticação (401 Unauthorized).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.warn("Falha na autenticação: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErroMensagem.NAO_AUTORIZADO.getMensagem(), request);
    }

    /**
     * Captura exceções de acesso negado (403 Forbidden).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Acesso negado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ErroMensagem.ACESSO_NEGADO.getMensagem(), request);
    }

    /**
     * Captura exceções genéricas para evitar que o usuário veja um erro inesperado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor." + ex.getMessage(), request);
    }

    /**
     * Método auxiliar para padronizar a resposta de erro.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
