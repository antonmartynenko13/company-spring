package com.martynenko.anton.company.project;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;
import static com.martynenko.anton.company.utils.Constants.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(name = "Project")
public record ProjectDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id,
    @Schema(required = true, description = "Unique")
    @NotNull(message = "Title is mandatory")
    @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
    String title,

    @Schema(required = true)
    @JsonFormat(pattern = DATE_FORMAT)
    @NotNull(message = "startDate is mandatory")
    LocalDate startDate,

    @JsonFormat(pattern = DATE_FORMAT)
    LocalDate endDate
) {

  public Project createInstance() {
    return new Project().update(this);
  }
}
