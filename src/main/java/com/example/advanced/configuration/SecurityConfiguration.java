package com.example.advanced.configuration;

import com.example.advanced.jwt.AccessDeniedHandlerException;
import com.example.advanced.jwt.AuthenticationEntryPointException;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

  @Value("${jwt.secret}")
  String SECRET_KEY;
  private final TokenProvider tokenProvider;
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationEntryPointException authenticationEntryPointException;
  private final AccessDeniedHandlerException accessDeniedHandlerException;

  private final CorsConfig corsConfig;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }



  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.cors();
    http
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("script-src 'self'");

    http.csrf().disable()

        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPointException)
        .accessDeniedHandler(accessDeniedHandlerException)

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and().httpBasic().disable()
        .authorizeRequests().antMatchers("/","/**").permitAll()
        .antMatchers("/api/members/**").permitAll()
        .antMatchers("/api/posts").permitAll()
        .antMatchers("/api/posts/**").permitAll()
        .antMatchers("/api/comments/**").permitAll()
        .antMatchers( "/v2/api-docs",
                "/h2-console/**",
                "/h2-console",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui/**").permitAll()
        .anyRequest().authenticated()


        .and()
        .addFilter(corsConfig.corsFilter())
        .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));



    return http.build();
  }

}
