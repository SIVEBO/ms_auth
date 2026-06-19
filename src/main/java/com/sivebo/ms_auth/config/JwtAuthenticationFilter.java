package com.sivebo.ms_auth.config;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sivebo.ms_auth.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(
                        @NonNull HttpServletRequest request,
                        @NonNull HttpServletResponse response,
                        @NonNull FilterChain filterChain) throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                        String token = header.substring(7);

                        if (jwtUtil.isTokenValid(token)) {
                                String username = jwtUtil.extractUsername(token);
                                String rol = jwtUtil.extractRol(token);

                                var authToken = new UsernamePasswordAuthenticationToken(
                                                username,
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase())));

                                SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                }

                filterChain.doFilter(request, response);
        }
}
