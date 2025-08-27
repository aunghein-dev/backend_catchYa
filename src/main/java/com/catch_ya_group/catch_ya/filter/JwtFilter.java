package com.catch_ya_group.catch_ya.filter;

import com.catch_ya_group.catch_ya.service.auth.JWTService;
import com.catch_ya_group.catch_ya.service.auth.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;

    /** Don’t run JWT auth for public endpoints and WS handshake */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || p.startsWith("/public/")
                || p.startsWith("/ws")
                || p.startsWith("/error")
                || p.startsWith("/swagger-ui")
                || p.startsWith("/v3/api-docs")
                || p.startsWith("/v2/api-docs")
                || p.startsWith("/swagger-resources")
                || p.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(req);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtService.extractphoneNo(token); // adjust if needed
                if (username != null) {
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, user)) {
                        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ex) {
                // Fail-soft: ignore bad tokens; do NOT block public/WS or cause 500s
                // log.debug("JWT ignored: {}", ex.getMessage());
            }
        }

        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) return h.substring(7);
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("token".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}
