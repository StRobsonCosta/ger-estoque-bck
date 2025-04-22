package com.kavex.xtoke.controle_estoque.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTokenService redisTokenService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        log.info("Validando o Token do Usuário pelo JWT Filter (doFilter) ");

        String token = jwtService.extrairToken(request);

        if (token != null && jwtService.validarToken(token)) {
            String userId = jwtService.extrairUserId(token);

            log.info("Verificando se o token está registrado no Redis");
            String tokenRedis = redisTokenService.obterToken(userId);
            log.info("Tokens: JWT -> {}, Redis -> {}", token, tokenRedis);

            if (Objects.isNull(tokenRedis) || !token.equals(tokenRedis)) {
                log.warn("Token não registrado no Redis, o fluxo será interrompido");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Evita reautenticação caso já esteja autenticado
            if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                log.info("Usuário já autenticado, o fluxo deve seguir.");
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                if (jwtService.validarToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            (UsernamePasswordAuthenticationToken) jwtService.getAuthentication(token, userDetails);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        log.info("Contexto de segurança antes de continuar o fluxo: {}", SecurityContextHolder.getContext().getAuthentication());

        chain.doFilter(request, response);
    }


}
