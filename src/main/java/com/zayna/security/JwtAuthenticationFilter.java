package com.zayna.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String JWT_ERROR_ATTRIBUTE = "zayna.jwt.error";
    public static final String ERROR_EXPIRED = "EXPIRED";
    public static final String ERROR_INVALID = "INVALID";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(BEARER_PREFIX.length()).trim();
            try {
                JwtService.AccessTokenClaims claims = jwtService.parseAccessToken(token);
                CustomUserDetails details = userDetailsService.loadUserById(claims.userId());

                boolean valid = details.isEnabled()
                        && details.isAccountNonLocked()
                        && details.getTokenVersion() == claims.tokenVersion();

                if (valid) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    request.setAttribute(JWT_ERROR_ATTRIBUTE, ERROR_INVALID);
                }
            } catch (ExpiredJwtException ex) {
                request.setAttribute(JWT_ERROR_ATTRIBUTE, ERROR_EXPIRED);
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
                request.setAttribute(JWT_ERROR_ATTRIBUTE, ERROR_INVALID);
            }
        }

        filterChain.doFilter(request, response);
    }
}