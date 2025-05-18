package org.example.bankingapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // More explicit disable
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(
//                                "/v3/api-docs/**",        // OpenAPI JSON
//                                "/swagger-ui/**",         // Swagger static resources
//                                "/swagger-ui.html"       // Swagger UI HTML
//                        ).permitAll()
//                        .requestMatchers("/error").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/authenticate")
//                        .permitAll()
//                        .requestMatchers("/api/v1/auth/**")
//                        .permitAll()
//                        .anyRequest()
//                        .authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                );
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/**").permitAll());
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Ensure JWT filter is added here
        return http.build();

    }
}