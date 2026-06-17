package org.example.strayanimalrescuebackend.config;

import org.example.strayanimalrescuebackend.Filter.LoginCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginCheckFilter loginCheckFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // 使用新的配置方式禁用 CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/login", "/user/register",
                                "/shelters/register",  // 放行注册成为救助站接口
                                "/shelters/all",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**").permitAll()  // 登录和注册接口不需要认证
                        .anyRequest().authenticated())  // 其他所有请求都需要认证
                .addFilterBefore(loginCheckFilter, UsernamePasswordAuthenticationFilter.class);  // 在用户名和密码认证过滤器之前添加 JWT 过滤器

        return http.build();
    }

}
