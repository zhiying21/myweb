package com.zhiying.config;

import com.zhiying.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/site/**").permitAll()
                        .requestMatchers("/love/**").permitAll()
                        .requestMatchers("/resource/list").permitAll()
                        .requestMatchers("/resume/verify", "/resume/content").permitAll()
                        .requestMatchers("/ticket/create").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/document/list", "/document/detail/**").permitAll()
                        .requestMatchers("/message/list").permitAll()
                        // 管理员接口
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 需登录
                        .requestMatchers("/auth/me", "/auth/logout", "/auth/profile", "/auth/upload").authenticated()
                        .requestMatchers("/message/add", "/message/comment", "/message/comment/**").authenticated()
                        .requestMatchers("/document/upload").authenticated()
                        .requestMatchers("/comment/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
