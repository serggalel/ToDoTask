package com.internship.todotask.user.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SecurityConfig {

    private static final String ADMIN = "admin";

    private static final String USER = "user";

    private static final String USER_LINK = "/api/user";

    private static final String TASK_LINK = "/api/task";

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) {
        http
                .cors(cors -> corsConfigurationSource())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(USER_LINK + "/register")
                        .permitAll()
                        .requestMatchers(USER_LINK + "/me").authenticated()
                        .requestMatchers(USER_LINK + "/all").hasAuthority(ADMIN)
                        .requestMatchers(USER_LINK + "/get/{userId}").hasAuthority(ADMIN)
                        .requestMatchers(USER_LINK + "/update").hasAuthority(ADMIN)
                        .requestMatchers(USER_LINK + "/delete/{userId}").hasAuthority(ADMIN)
                        .requestMatchers(USER_LINK + "/getCollabs/{taskId}").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(USER_LINK + "/getPossCollabs/{taskId}").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(USER_LINK + "/rmvCollab").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(USER_LINK + "/addCollab").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(TASK_LINK + "/create").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(TASK_LINK + "/update").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(TASK_LINK + "/getByUser/{userId}").hasAnyAuthority(ADMIN, USER)
                        .requestMatchers(TASK_LINK + "/delete/{taskId}").hasAnyAuthority(ADMIN, USER)
                        .anyRequest().authenticated()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
