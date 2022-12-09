package com.martynenko.anton.company.department;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DepartmentDTOTest {
  private static Validator validator;

  @BeforeAll
  public static void setupValidatorInstance() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void withNullMandatoryFieldsShouldReturnViolations() {
    DepartmentDTO dto = new DepartmentDTO(null, null);
    Set<String> notValidProps = Set.of("title");
    Set<ConstraintViolation<DepartmentDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withEmptyMandatoryFieldsShouldReturnViolations() {
    DepartmentDTO dto = new DepartmentDTO(null, "");
    Set<String> notValidProps = Set.of("title");

    Set<ConstraintViolation<DepartmentDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withTooLongStringFieldsShouldReturnViolations() {
    String longString = getString(31);
    DepartmentDTO dto = new DepartmentDTO(null, longString);
    Set<String> notValidProps = Set.of("title");

    Set<ConstraintViolation<DepartmentDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void shouldValidateSuccessfully() {
    DepartmentDTO dto = new DepartmentDTO(null, getString(10));
    Set<ConstraintViolation<DepartmentDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }

  String getString(int length) {
    String s = IntStream.range(40, 40 + length)
        .mapToObj(i -> Character.toString((char)i))
        .collect(Collectors.joining());
    return s;
  }
}