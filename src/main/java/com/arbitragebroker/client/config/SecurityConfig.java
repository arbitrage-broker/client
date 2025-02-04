package com.arbitragebroker.client.config;

import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.service.HCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.arbitragebroker.client.enums.RoleType.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final transient UserDetailsService userDetailsService;
    private final SuccessLoginConfig successLoginConfig;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final HCaptchaService hCaptchaService;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/index", "/aboutus", "/ourservices", "/ltr/**", "/logout",
                                "/page_404", "/page_200", "/page_403", "/region_denied", "/login",
                                "/registration", "/send-OTP", "/reset-pass", "/api/v1/country/findAllSelect*",
                                "/api/v1/user/verify-email/**", "/api/v1/user/register*", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user*/**").hasAnyRole(
                                RoleType.name(ADMIN), RoleType.name(SUPER_WISER),
                                RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers(HttpMethod.POST, "/api/v1/wallet*/**", "/api/v1/arbitrage*/**",
                                "/api/v1/notification*/**", "/api/v1/subscription/purchase", "/api/v1/files")
                        .hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER),
                                RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers(HttpMethod.GET, "/dashboard", "/subUsers", "/deposit", "/profile",
                                "/withdrawal", "/notification", "/arbitrage", "/about", "/referral-reward",
                                "/api/v1/files/**", "/api/v1/common/**", "/api/v1/wallet/**", "/api/v1/coin/**",
                                "/api/v1/exchange/**", "/api/v1/parameter/**", "/api/v1/subscription/**",
                                "/api/v1/subscription-package/**", "/api/v1/user/**", "/api/v1/role/**",
                                "/api/v1/arbitrage/**", "/api/v1/notification/**", "/api/v1/role-detail/**")
                        .hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER),
                                RoleType.name(MANAGER), RoleType.name(USER))
                        .requestMatchers("/**").hasRole(RoleType.name(ADMIN))
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?errorMsg=invalidUserNameOrPassword")
//                        .successHandler(successLoginConfig)
                        .defaultSuccessUrl("/dashboard", true)
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index")
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION") // Don't delete JSESSIONID
                )
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/index"))
//                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // Ensures session ID is retain
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .invalidSessionUrl("/index")
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/index")
                )
        ;

        return http.build();
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