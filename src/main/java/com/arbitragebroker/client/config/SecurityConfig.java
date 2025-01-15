package com.arbitragebroker.client.config;

import com.arbitragebroker.client.service.impl.CustomUserDetailsServiceImpl;
import com.arbitragebroker.client.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;
    @Autowired
    SuccessLoginConfig successLoginConfig;
    @Autowired
    CustomAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HoneypotAuthenticationFilter honeypotFilter =
                new HoneypotAuthenticationFilter("website");
        http.addFilterBefore(honeypotFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers("/index","/aboutus","/ourservices","/ltr/**","/logout","/page_404","/page_200","/page_403","/region_denied","/login","/registration","/send-OTP","/reset-pass","/api/v1/country/findAllSelect*","/api/v1/user/verify-email/**","/api/v1/user/register*","/actuator/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/api/v1/user*/**").hasAnyRole(RoleType.name(RoleType.ADMIN),RoleType.name(RoleType.SUPER_WISER),RoleType.name(RoleType.MANAGER), RoleType.name(RoleType.USER))
                .antMatchers(HttpMethod.POST, "/api/v1/wallet*/**","/api/v1/arbitrage*/**","/api/v1/notification*/**","/api/v1/subscription/purchase","/api/v1/files").hasAnyRole(RoleType.name(RoleType.ADMIN),RoleType.name(RoleType.SUPER_WISER),RoleType.name(RoleType.MANAGER), RoleType.name(RoleType.USER))
                .antMatchers(HttpMethod.GET,  "/dashboard","/subUsers","/deposit","/profile","/withdrawal","/notification","/arbitrage","/about","/referral-reward","/api/v1/files/**", "/api/v1/common/**","/api/v1/wallet/**","/api/v1/coin/**","/api/v1/exchange/**","/api/v1/parameter/**","/api/v1/subscription/**","/api/v1/subscription-package/**","/api/v1/user/**","/api/v1/role/**","/api/v1/arbitrage/**","/api/v1/notification/**","/api/v1/role-detail/**").hasAnyRole(RoleType.name(RoleType.ADMIN),RoleType.name(RoleType.SUPER_WISER),RoleType.name(RoleType.MANAGER), RoleType.name(RoleType.USER))
                .antMatchers("/**").hasRole(RoleType.name(RoleType.ADMIN))
                .anyRequest().authenticated()
                .and().csrf().disable()
                .formLogin()
                .loginPage("/login").failureUrl("/login?errorMsg=invalidUserNameOrPassword")
                .successHandler(successLoginConfig)
                .usernameParameter("login") // This will accept either email or username
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID", "SESSION").invalidateHttpSession(true)
                .logoutSuccessUrl("/index")
                .and().exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/index"))
                .accessDeniedHandler(accessDeniedHandler)
                .and().sessionManagement()
                .sessionFixation().newSession()
                .invalidSessionUrl("/index")
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/index")
        ;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}