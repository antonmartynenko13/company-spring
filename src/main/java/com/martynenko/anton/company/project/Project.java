package com.martynenko.anton.company.project;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  @NotNull
  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
  private String title;
  @Column(nullable = false)
  @NotNull
  private LocalDate startDate;

  private LocalDate endDate;

  public Project(final ProjectDTO projectDTO) {
    this.title = projectDTO.title();
    this.startDate = projectDTO.startDate();
    this.endDate = projectDTO.endDate();
  }

  public ProjectDTO toDTO() {
    return new ProjectDTO(this.id,
        this.title,
        this.startDate,
        this.endDate);
  }

  public Project update(final ProjectDTO projectDTO) {
    this.title = projectDTO.title();
    this.startDate = projectDTO.startDate();
    this.endDate = projectDTO.endDate();
    return this;
  }
}
