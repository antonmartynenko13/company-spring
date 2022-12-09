package com.martynenko.anton.company.exception;

import javax.annotation.Priority;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

/*
  Required controller advice for mapping security exceptions on problems
* */

@RestControllerAdvice
@Priority(1)
public class SecurityExceptionHandler implements SecurityAdviceTrait { }
