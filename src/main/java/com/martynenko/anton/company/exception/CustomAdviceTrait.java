package com.martynenko.anton.company.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

/*
* Custom Advice trait for more agile mapping of some exceptions
*/
public interface CustomAdviceTrait extends AdviceTrait {

  String CONFLICT_MESSAGE = "The request cannot be completed due to duplicate unique fields "
      + "or other inconsistencies.";

  @ExceptionHandler(EntityNotFoundException.class)
  default ResponseEntity<Problem> handleEntityNotFoundException(
      final EntityNotFoundException exception, final NativeWebRequest request) {
    return create(Status.NOT_FOUND, exception, request);
  }

  @ExceptionHandler({DataIntegrityViolationException.class,
      /*H2 compatibility*/InvalidDataAccessApiUsageException.class})
  default ResponseEntity<Problem> handleDataIntegrityViolationException(final Exception exception,
      final NativeWebRequest request) {
    return create(Status.CONFLICT, new IllegalArgumentException(CONFLICT_MESSAGE), request);
  }
}
