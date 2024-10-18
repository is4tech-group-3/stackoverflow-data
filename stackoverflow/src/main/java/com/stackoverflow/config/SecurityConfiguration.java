package com.stackoverflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String BASE_PUBLICATION = "/api/v1/publication";
    private static final String BASE_COMMENT = "/api/v1/comment";
    private static final String BASE_QUESTION = "/api/v1/question";
    private static final String BASE_ANSWER = "/api/v1/answer";

    private static final String PUBLICATION = "PUBLICATION";
    private static final String COMMENT = "COMMENT";
    private static final String QUESTION = "QUESTION";
    private static final String ANSWER = "ANSWER";

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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, BASE_PUBLICATION).hasRole(PUBLICATION)
                        .requestMatchers(HttpMethod.GET, BASE_PUBLICATION + "/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_PUBLICATION).permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_PUBLICATION + "/findByTag/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, BASE_PUBLICATION + "/*").hasRole(PUBLICATION)
                        .requestMatchers(HttpMethod.DELETE, BASE_PUBLICATION + "/*").hasRole(PUBLICATION)

                        .requestMatchers(HttpMethod.POST, BASE_COMMENT + "/*").hasAnyRole(PUBLICATION, COMMENT)
                        .requestMatchers(HttpMethod.GET, BASE_COMMENT + "/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_COMMENT + "/findById/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, BASE_COMMENT + "/*").hasAnyRole(PUBLICATION, COMMENT)
                        .requestMatchers(HttpMethod.DELETE, BASE_COMMENT + "/*").hasAnyRole(PUBLICATION, COMMENT)

                        .requestMatchers(HttpMethod.POST, BASE_QUESTION).hasRole(QUESTION)
                        .requestMatchers(HttpMethod.GET, BASE_QUESTION + "/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_QUESTION).permitAll()
                        .requestMatchers(HttpMethod.PUT, BASE_QUESTION + "/*").hasRole(QUESTION)
                        .requestMatchers(HttpMethod.DELETE, BASE_QUESTION + "/*").hasRole(QUESTION)

                        .requestMatchers(HttpMethod.POST, BASE_ANSWER + "/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.POST, BASE_ANSWER + "/like/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.GET, BASE_ANSWER + "/findById/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_ANSWER + "/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_ANSWER + "/verifiedByQuestion/*").permitAll()
                        .requestMatchers(HttpMethod.GET, BASE_ANSWER + "/likes/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, BASE_ANSWER + "/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.PATCH, BASE_ANSWER + "/verified/*/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.PATCH, BASE_ANSWER + "/unverified/*/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.DELETE, BASE_ANSWER + "/*").hasAnyRole(QUESTION, ANSWER)
                        .requestMatchers(HttpMethod.DELETE, BASE_ANSWER + "/dislike/*").hasAnyRole(QUESTION, ANSWER)
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
