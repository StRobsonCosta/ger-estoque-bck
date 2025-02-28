package com.kavex.xtoke.controle_estoque.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTokenService redisTokenService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Para buscar informações do usuário no banco

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = jwtService.extrairToken(request);

        if (token != null && jwtService.validarToken(token)) {
            String userId = jwtService.extrairUserId(token);

            // Verifica se o token está registrado no Redis
            String tokenRedis = redisTokenService.obterToken(userId);
            if (tokenRedis == null || !token.equals(tokenRedis)) {
                chain.doFilter(request, response);
                return;
            }

            // Evita reautenticação caso já esteja autenticado
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                if (jwtService.validarToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            (UsernamePasswordAuthenticationToken) jwtService.getAuthentication(token, userDetails);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }

}
