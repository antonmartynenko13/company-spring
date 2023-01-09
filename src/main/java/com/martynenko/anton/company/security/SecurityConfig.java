package com.martynenko.anton.company.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@Import(SecurityProblemSupport.class)
public class SecurityConfig {

  private final SecurityProblemSupport problemSupport;

  @Autowired
  public SecurityConfig(final SecurityProblemSupport problemSupport) {
    this.problemSupport = problemSupport;
  }

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

    http
        .cors()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests(configurer ->
            configurer
                .antMatchers("/api/**").authenticated()
                .antMatchers("/").permitAll()
        )
        .oauth2ResourceServer().jwt();
    //Problem support
    http.exceptionHandling()
        .authenticationEntryPoint(problemSupport)
        .accessDeniedHandler(problemSupport);
    return http.build();
  }
}
