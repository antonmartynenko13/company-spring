package com.martynenko.anton.company.department;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(name = "Department")
public record DepartmentDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id,

    @Schema(required = true, description = "Unique")
    @NotNull(message = "Title is mandatory")
    @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
    String title
) {

  public Department createInstance() {
    return new Department().update(this);
  }
}
