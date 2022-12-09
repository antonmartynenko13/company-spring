package com.martynenko.anton.company.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Configuration
public class JacksonConfig {

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper().registerModules(
        // zelando problem compatibility
        new ProblemModule(),
        new ConstraintViolationProblemModule(),

        // java8.time compatibility
        new JavaTimeModule());
  }
}
