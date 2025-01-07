package com.precious.TaskApi.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.precious.TaskApi.dto.UserValidationResponse;
import com.precious.TaskApi.feign.UserServiceClient;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final UserServiceClient userServiceClient;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
            
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            ResponseEntity<UserValidationResponse> validationResponse = 
                userServiceClient.validateToken(request);
            
            if (validationResponse.getStatusCode().is2xxSuccessful() && 
                validationResponse.getBody().isValid()) {
                    
                UserValidationResponse userDetails = validationResponse.getBody();
                
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + userDetails.getRole())
                );
                
                // Create custom user details with userId
                CustomUserDetails customUserDetails = new CustomUserDetails(
                    userDetails.getUserId(),
                    userDetails.getRole(),
                    authorities
                );
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        customUserDetails, // principal is now CustomUserDetails
                        null,
                        authorities
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}