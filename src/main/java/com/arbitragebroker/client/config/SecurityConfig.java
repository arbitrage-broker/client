package com.arbitragebroker.client.config;

import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.service.HCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.arbitragebroker.client.enums.RoleType.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final HCaptchaService hCaptchaService;
    @Value("${site.url}")
    private String siteURL;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://" + siteURL,
                "https://localhost:2025",  // For local development
                "http://localhost:3000"   // For local development
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Static resources
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Public endpoints
                        .requestMatchers("/", "/index", "/aboutus", "/ourservices", "/ltr/**",
                                "/page_404", "/page_200", "/page_403", "/region_denied", "/login",
                                "/registration", "/send-OTP", "/reset-pass", "/api/v1/country/findAllSelect*",
                                "/api/v1/user/verify-email/**", "/api/v1/user/register*", "/actuator/**").permitAll()
                        // Protected endpoints
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user*/**").hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER), RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers(HttpMethod.POST, "/api/v1/wallet*/**","/api/v1/arbitrage*/**","/api/v1/notification*/**","/api/v1/subscription/purchase","/api/v1/files").hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER), RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers(HttpMethod.GET, "/dashboard","/subUsers","/deposit","/profile","/withdrawal","/notification","/arbitrage","/about","/referral-reward","/api/v1/files/**", "/api/v1/common/**","/api/v1/wallet/**","/api/v1/coin/**","/api/v1/exchange/**","/api/v1/parameter/**","/api/v1/subscription/**","/api/v1/subscription-package/**","/api/v1/user*/**","/api/v1/role/**","/api/v1/arbitrage/**","/api/v1/notification/**","/api/v1/role-detail/**").hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER),RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers("/**").hasRole(RoleType.name(ADMIN))
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?errorMsg=invalidUserNameOrPassword")
                        .defaultSuccessUrl("/dashboard")
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("SESSION","JSESSIONID")
                )
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (isAjaxRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("Unauthorized");
                            } else {
                                response.sendRedirect("/login?errorMsg=unauthorizedSession");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (isAjaxRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.getWriter().write("Access Denied");
                            } else {
                                response.sendRedirect("/page_403");
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?errorMsg=sessionExpired")
                        .sessionRegistry(sessionRegistry())
                );

        return http.build();
    }
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest");
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter("website", hCaptchaService);
    }
}