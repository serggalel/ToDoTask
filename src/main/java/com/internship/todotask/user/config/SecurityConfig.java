package com.internship.todotask.user.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = "admin";

    private static final String USER = "user";

    private static final String LINK = "/api/user";

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(LINK + "/register")
                        .permitAll()
                        .requestMatchers(LINK + "/all").hasAuthority(ADMIN)
                        .requestMatchers(LINK + "/get/{userId}").hasAuthority(ADMIN)
                        .requestMatchers(LINK + "/update/{userId}").hasAuthority(ADMIN)
                        .requestMatchers(LINK + "/delete/{userId}").hasAuthority(ADMIN)
                        .requestMatchers(LINK + "/getCollabs/{taskId}").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(LINK + "/getPossCollabs/{taskId}").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(LINK + "/rmvCollab").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(LINK + "/addCollab").hasAnyAuthority(ADMIN, USER)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
