package com.martynenko.anton.company.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/*
  Required controller advice for mapping exceptions on problems
* */
@RestControllerAdvice
public class CommonExceptionHandler implements ProblemHandling, CustomAdviceTrait {

}
