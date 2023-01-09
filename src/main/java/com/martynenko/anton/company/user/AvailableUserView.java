package com.martynenko.anton.company.user;

import static com.martynenko.anton.company.utils.Constants.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(name = "Available user")
public record AvailableUserView(
    @Schema(required = true)
    @JsonProperty("user_details")
    UserDTO userDTO,
    @Schema(required = true)
    @JsonProperty("available_from")
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDate availableFrom,

    @Schema(description = "Optional")
    @JsonProperty("available_to")
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDate availableTo
) {
  public AvailableUserView {
    Objects.requireNonNull(userDTO);
    Objects.requireNonNull(availableFrom);
  }
}
