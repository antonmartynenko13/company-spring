package com.martynenko.anton.company.projectposition;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;
import static com.martynenko.anton.company.utils.Constants.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(name = "Project position")
public record ProjectPositionDTO(@Schema(accessMode = Schema.AccessMode.READ_ONLY)
                                  Long id,
                                 @Schema(required = true, description = "Present users id")
                                 @NotNull(message = "userId is mandatory")
                                 @Min(value = 1L, message = "The value must be positive")
                                 Long userId,
                                 @Schema(required = true, description = "Present projects id")
                                  @NotNull(message = "projectId is mandatory")
                                  @Min(value = 1L, message = "The value must be positive")
                                 Long projectId,
                                 @Schema(required = true)
                                  @JsonFormat(pattern = DATE_FORMAT)
                                  @NotNull(message = "positionStartDate is mandatory")
                                 LocalDate positionStartDate,
                                  @JsonFormat(pattern = DATE_FORMAT)
                                 LocalDate positionEndDate,
                                 @Schema(required = true)
                                  @NotNull(message = "positionTitle is mandatory")
                                  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
                                 String positionTitle,
                                  @NotNull(message = "occupation is mandatory")
                                  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
                                 String occupation) {

  public ProjectPosition createInstance(final User user, final Project project) {
    return new ProjectPosition().update(this, user, project);
  }
}
