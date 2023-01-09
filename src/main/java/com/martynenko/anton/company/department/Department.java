package com.martynenko.anton.company.department;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;

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
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@ToString
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  @NotNull
  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
  private String title;

  public Department(final DepartmentDTO departmentDTO) {
    this.title = departmentDTO.title();
  }

  Department update(final DepartmentDTO departmentDTO) {
    this.title = departmentDTO.title();
    return this;
  }

  DepartmentDTO toDTO() {
    return new DepartmentDTO(id, title);
  }
}
