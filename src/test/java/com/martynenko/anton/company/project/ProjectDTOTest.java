package com.martynenko.anton.company.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.martynenko.anton.company.department.DepartmentDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectDTOTest {
  private static Validator validator;

  @BeforeAll
  public static void setupValidatorInstance() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void withNullMandatoryFieldsShouldReturnViolations() {
    ProjectDTO dto = new ProjectDTO(null, null, null, null);
    Set<String> notValidProps = Set.of("title", "startDate");
    Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withEmptyMandatoryFieldsShouldReturnViolations() {
    ProjectDTO dto = new ProjectDTO(null, "", mock(LocalDate.class), null);
    Set<String> notValidProps = Set.of("title");

    Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withTooLongStringFieldsShouldReturnViolations() {
    String longString = getString(31);
    ProjectDTO dto = new ProjectDTO(null, longString, mock(LocalDate.class), null);
    Set<String> notValidProps = Set.of("title");

    Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void shouldValidateSuccessfully() {
    //valid dto
    ProjectDTO dto = new ProjectDTO(null, getString(10), LocalDate.now(), null);
    Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }

  String getString(int length) {
    String s = IntStream.range(40, 40 + length)
        .mapToObj(i -> Character.toString((char)i))
        .collect(Collectors.joining());
    return s;
  }
}