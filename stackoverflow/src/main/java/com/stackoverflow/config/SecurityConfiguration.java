package com.stackoverflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/publication").hasRole("PUBLICATION")
                        .requestMatchers(HttpMethod.GET,"/api/v1/publication/*").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/publication").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/publication/findByTag/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/publication/*").hasRole("PUBLICATION")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/publication/*").hasRole("PUBLICATION")

                        .requestMatchers(HttpMethod.POST, "/api/v1/comment/*").hasAnyRole("PUBLICATION", "COMMENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/comment/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/comment/findById/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/comment/*").hasAnyRole("PUBLICATION", "COMMENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comment/*").hasAnyRole("PUBLICATION", "COMMENT")

                        .requestMatchers(HttpMethod.POST, "/api/v1/question").hasRole("QUESTION")
                        .requestMatchers(HttpMethod.GET, "/api/v1/question/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/question").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/question/*").hasRole("QUESTION")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/question/*").hasRole("QUESTION")

                        .requestMatchers(HttpMethod.POST, "/api/v1/answer/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/answer/like/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/answer/findById/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/answer/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/answer/verifiedByQuestion/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/answer/likes/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/answer/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/answer/verified/*/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/answer/unverified/*/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/answer/*").hasAnyRole("QUESTION", "ANSWER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/answer/dislike/*").hasAnyRole("QUESTION", "ANSWER")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
