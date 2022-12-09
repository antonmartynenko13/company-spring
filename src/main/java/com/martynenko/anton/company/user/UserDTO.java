package com.martynenko.anton.company.user;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;
import static com.martynenko.anton.company.utils.Constants.EMAIL_PATTERN;

import com.martynenko.anton.company.department.Department;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(name = "User")
public record UserDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id,
    @Schema(required = true)
    @NotNull(message = "firstName is mandatory")
    @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
    String firstName,
    @Schema(required = true)
    @NotNull(message = "lastName is mandatory")
    @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
    String lastName,
    @Schema(required = true, description = "Unique")
    @Pattern(regexp = EMAIL_PATTERN,
        message = "Email is invalid")
    @NotNull
    String email,
    @Schema(required = true)
    @NotNull(message = "jobTitle is mandatory")
    @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
    String jobTitle,
    @Schema(required = true, description = "Present department id")
    @NotNull(message = "departmentId is mandatory")
    @Min(value = 1L, message = "The value must be positive")
    Long departmentId
) {

  public User createInstance(final Department department) {
    return new User().update(this, department);
  }
}
