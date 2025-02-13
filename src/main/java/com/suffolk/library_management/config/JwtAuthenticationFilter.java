package com.suffolk.library_management.config;

import com.suffolk.library_management.repository.TokenRepository;
import com.suffolk.library_management.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final JwtService jwtService;
    final UserDetailsService userDetailsService;
    final TokenRepository tokenRepository;
    final AuthService authenticationService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        filterRequest(request, response, filterChain);
    }


    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(message);
    }


    private void filterRequest(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (request.getServletPath().contains("/api/v1/auth")
                || request.getServletPath().contains("/api/v1/health")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                jwt = authHeader.substring(7);
                userEmail = jwtService.extractUsername(jwt);
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // checking if the user is not authenticated and userEmail is not null
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    var isTokenValid = tokenRepository.findByToken(jwt)
                            .map(t -> !t.isExpired() && !t.isRevoked())
                            .orElse(false);
                    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        logger.error("Invalid token");
                        handleException(response, "Invalid token");
                        return;
                    }
                } else {
                    logger.error("Invalid user details in token");
                    handleException(response, "Invalid token");
                    return;
                }
            } catch (ExpiredJwtException e) {
                logger.error("Token expired : {}", String.valueOf(e));
                handleException(response, "Token expired");
                return;
            } catch (JwtException e) {
                logger.error("Invalid token : {}", String.valueOf(e));
                handleException(response, "Invalid token");
                return;
            }
        } else {
            logger.error("No Auth header found");
            handleException(response, "No Auth header found");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
