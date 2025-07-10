package com.grepp.teamnotfound.infra.config;

import com.grepp.teamnotfound.infra.auth.token.filter.AuthExceptionFilter;
import com.grepp.teamnotfound.infra.auth.token.filter.JwtAuthenticationFilter;
import com.grepp.teamnotfound.infra.auth.token.filter.LogoutFilter;
import com.grepp.teamnotfound.infra.util.requestmatcher.RequestMatcherHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthExceptionFilter authExceptionFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutFilter logoutFilter;
    private final RequestMatcherHolder requestMatcherHolder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        RequestMatcher permitAllMatcher = requestMatcherHolder.getRequestMatchersByMinRole(null);
        RequestMatcher adminMatcher = requestMatcherHolder.getRequestMatchersByMinRole("ADMIN");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
//                .oauth2Login(oauth ->
//                        oauth.successHandler(oAuth2SuccessHandler)
//                                .failureHandler(oAuth2FailureHandler)
//
//                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize

                                .requestMatchers(permitAllMatcher).permitAll()
                                .requestMatchers(adminMatcher).hasRole("ADMIN")

                                //.requestMatchers(GET, "/", "/error", "/favicon.ico").permitAll()
                                //.requestMatchers(POST, "/api/auth/v1/register/**","/api/auth/v1/login",
                                //        "/api/auth/v1/admin/register", "/api/auth/v1/admin/login").permitAll()
//                                .requestMatchers(POST, "/api/v1/auth/logout").permitAll()

                                //.requestMatchers(GET, "/**").permitAll()
                                //.requestMatchers(GET, "/swagger-ui", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // 프리플라이트 허용
                                .anyRequest().authenticated()
                )

                .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
                // TODO 프론트 서버로 수정 필요
                "http://localhost:3000",
                "https://mungnote-172598302113.asia-northeast3.run.app"
        ));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
