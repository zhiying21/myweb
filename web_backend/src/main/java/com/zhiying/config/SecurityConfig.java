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
                        // 健康检查
                        .requestMatchers("/health/**").permitAll()
                        // 认证接口
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        // 网站配置（公开）
                        .requestMatchers("/site/**").permitAll()
                        // 恋爱日记（公开）
                        .requestMatchers("/love/**").permitAll()
                        // 资源列表（公开）
                        .requestMatchers("/resource/list").permitAll()
                        // 简历（公开 - 由前端控制密码）
                        .requestMatchers("/resume/content").permitAll()
                        // 工单提交（公开）
                        .requestMatchers("/ticket/create").permitAll()
                        // 上传文件访问（公开）
                        .requestMatchers("/uploads/**").permitAll()
                        // 文档列表/详情（公开）
                        .requestMatchers("/document/list", "/document/detail/**").permitAll()
                        // 文档评论列表（公开）
                        .requestMatchers("/document/*/comments").permitAll()
                        // 留言列表（公开）
                        .requestMatchers("/message/list").permitAll()
                        // Swagger（公开）
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        // 管理员接口
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 需要登录的接口
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
