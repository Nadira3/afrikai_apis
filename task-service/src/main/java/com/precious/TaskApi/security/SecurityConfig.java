package com.precious.TaskApi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.precious.TaskApi.security.SecurityContextFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
	    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    .addFilterBefore(securityContextFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
		.requestMatchers("/api/tasks/client/**").hasRole("CLIENT")
                .requestMatchers("/api/tasks/user/**").hasRole("TASKER")
                .requestMatchers("/api/tasks/admin/**").hasRole("ADMIN")
		.requestMatchers(
			"/swagger-ui/**",
			"/swagger-ui.html/**",
			"/webjars/**",
			"/v3/api-docs/**"
		)
		.permitAll() // Allow access to the Swagger UI and the authentication endpoint
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public SecurityContextFilter securityContextFilter() {
	return new SecurityContextFilter();
    }
}
