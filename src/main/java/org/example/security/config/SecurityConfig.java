package org.example.security.config;

import org.example.security.filter.AuthenticationLoggingFilter;
import org.example.security.form.FormAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final FormAuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final AuthenticationLoggingFilter authenticationLoggingFilter;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          FormAuthenticationSuccessHandler formAuthenticationSuccessHandler,
                          AuthenticationLoggingFilter authenticationLoggingFilter) {
        this.authenticationProvider = authenticationProvider;
        this.formAuthenticationSuccessHandler = formAuthenticationSuccessHandler;
        this.authenticationLoggingFilter = authenticationLoggingFilter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/users/signup", "/api/users/**")
        );

        httpSecurity.authenticationProvider(authenticationProvider);

        httpSecurity.authorizeHttpRequests(c -> c
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/api-docs/swagger-config",
                        "/api-docs/**").permitAll()
                .requestMatchers(
                        "/actuator/**").permitAll()
                .requestMatchers(
                        "/api/users/signup",
                        "/api/circuit-breaker-test").permitAll()
                .requestMatchers(
                        "/api/students/**",
                        "/api/books/**",
                        "/api/users/read/**",
                        "/api/users/update",
                        "/api/users/delete/**",
                        "/api/users/readAll").permitAll()
                .anyRequest().denyAll()
        );

        httpSecurity.formLogin(c -> c.successHandler(formAuthenticationSuccessHandler));

//        httpSecurity.httpBasic(Customizer.withDefaults());

        httpSecurity.csrf(c -> c.disable());

        httpSecurity.addFilterAfter(authenticationLoggingFilter, BasicAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
