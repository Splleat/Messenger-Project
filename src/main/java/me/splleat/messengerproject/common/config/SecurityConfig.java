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

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    private static final String H2_CONSOLE_PATH = "/h2-console";

    private final JwtValidator jwtValidator;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final List<String> publicPaths;
    private final boolean h2ConsoleEnabled;
    private final List<String> allowedOrigins;

    @Autowired
    public SecurityConfig(
            JwtValidator jwtValidator,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            @Value("${jwt.public-paths}") List<String> publicPaths,
            @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled,
            @Value("${cors.allowed-origins}") List<String> allowedOrigins)
    {
        this.jwtValidator = jwtValidator;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.h2ConsoleEnabled = h2ConsoleEnabled;
        this.allowedOrigins = allowedOrigins.stream()
                .map(String::trim)
                .toList();

        List<String> paths = new ArrayList<>(publicPaths.stream()
                .map(String::trim)
                .toList());

        if (h2ConsoleEnabled) {
            paths.add(H2_CONSOLE_PATH);
        }

        this.publicPaths = List.copyOf(paths);
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
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/auth/login", "/auth/register", "/auth/refresh").permitAll()
                            .requestMatchers("/ws-stomp/**").permitAll()
                            .requestMatchers("/actuator/health").permitAll();
                    if (h2ConsoleEnabled) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        if (h2ConsoleEnabled) {
            http.headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        }
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
