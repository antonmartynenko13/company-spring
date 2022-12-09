package com.martynenko.anton.company.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.martynenko.anton.company.projectposition.ProjectPositionDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserDTOTest {
  private static Validator validator;

  @BeforeAll
  public static void setupValidatorInstance() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void withNullMandatoryFieldsShouldReturnViolations() {
    UserDTO dto = new UserDTO(
        null, null, null, null, null, null);
    Set<String> notValidProps = Set.of(
        "firstName", "lastName", "email", "jobTitle", "departmentId");
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withEmptyMandatoryFieldsAndNegativeIdsShouldReturnViolations() {
    UserDTO dto = new UserDTO(
        null, "", "", "", "", -1L);
    Set<String> notValidProps = Set.of(
        "firstName", "lastName", "email", "jobTitle", "departmentId");

    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withTooLongStringFieldsShouldReturnViolations() {
    String longString = getString(31);
    String longEmail = longString + "@domain.com";
    UserDTO dto = new UserDTO(
        null, longString, longString, longEmail, longString, 1L);
    Set<String> notValidProps = Set.of(
        "firstName", "lastName", "email", "jobTitle");

    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void withNotValidEmailShouldReturnViolation() {
    String email = "userdomain.com";
    UserDTO dto = new UserDTO(
        null, getString(5), getString(5), email, getString(5), 1L);
    Set<String> notValidProps = Set.of(
        "email");

    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

    assertThat(violations).hasSize(notValidProps.size());
    violations.forEach(action -> assertThat(notValidProps.contains(action.getPropertyPath().toString())));
  }

  @Test
  void shouldValidateSuccessfully() {
    UserDTO dto = new UserDTO(
        null, getString(5), getString(5), "valid@gmail.com", getString(5), 1L);
    Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

    assertThat(violations).isEmpty();
  }

  String getString(int length) {
    String s = IntStream.range(40, 40 + length)
        .mapToObj(i -> Character.toString((char)i))
        .collect(Collectors.joining());
    return s;
  }
}