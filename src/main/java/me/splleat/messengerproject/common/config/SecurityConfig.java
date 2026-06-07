package me.splleat.messengerproject.common.config;

import me.splleat.messengerproject.infrastructure.security.JwtAuthenticationEntryPoint;
import me.splleat.messengerproject.infrastructure.security.JwtAuthenticationFilter;
import me.splleat.messengerproject.infrastructure.security.JwtValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    private final JwtValidator jwtValidator;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final List<String> publicPaths;

    @Autowired
    public SecurityConfig(
            JwtValidator jwtValidator,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            @Value("${jwt.public-paths}") List<String> publicPaths)
    {
        this.jwtValidator = jwtValidator;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.publicPaths = publicPaths.stream()
                .map(String::trim)
                .toList();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .cors(Customizer.withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(jwtValidator, publicPaths), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register", "/auth/refresh").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/ws-stomp/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
