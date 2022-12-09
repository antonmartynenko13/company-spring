package com.martynenko.anton.company.projectposition;

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

class ProjectPositionDTOTest {
  private static Validator validator;

  @BeforeAll
  public static void setupValidatorInstance() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void withNullMandatoryFieldsShouldReturnViolations() {
    ProjectPositionDTO dto = new ProjectPositionDTO(
        null, null, null, null, null, null, null);
    Set<String> notValidProps = Set.of(
        "userId", "projectId", "positionStartDate", "positionTitle", "occupation");
    Set<ConstraintViolation<ProjectPositionDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withEmptyMandatoryFieldsAndNegativeIdsShouldReturnViolations() {
    ProjectPositionDTO dto = new ProjectPositionDTO(
        null, -1L, -1L, mock(LocalDate.class), null, "", "");
    Set<String> notValidProps = Set.of(
         "userId", "projectId", "positionTitle", "occupation");

    Set<ConstraintViolation<ProjectPositionDTO>> violations = validator.validate(dto);
    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withTooLongStringFieldsShouldReturnViolations() {
    String longString = getString(31);
    ProjectPositionDTO dto = new ProjectPositionDTO(
        null, 1L, 1L, mock(LocalDate.class), null, longString, longString);
    Set<String> notValidProps = Set.of(
        "positionTitle", "occupation");

    Set<ConstraintViolation<ProjectPositionDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void shouldValidateSuccessfully() {
    ProjectPositionDTO dto = new ProjectPositionDTO(null, 1l, 1l, mock(LocalDate.class),
        mock(LocalDate.class), getString(10), getString(10));
    Set<ConstraintViolation<ProjectPositionDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }

  String getString(int length) {
    String s = IntStream.range(40, 40 + length)
        .mapToObj(i -> Character.toString((char)i))
        .collect(Collectors.joining());
    return s;
  }
}