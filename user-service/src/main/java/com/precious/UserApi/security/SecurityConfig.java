package com.precious.UserApi.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider  authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/eureka/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                // .requestMatchers("/error").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails cher = users
            .username("cherjoie")
            .password(passwordEncoder.encode("abc123"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(cher);
    }
}
