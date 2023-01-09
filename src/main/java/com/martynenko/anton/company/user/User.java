package com.martynenko.anton.company.user;

import static com.martynenko.anton.company.utils.Constants.DATABASE_STRINGS_MAX_SIZE;
import static com.martynenko.anton.company.utils.Constants.EMAIL_PATTERN;

import com.martynenko.anton.company.department.Department;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users") //'user' is reserved word
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false)
  @NotNull
  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
  private String firstName;


  @Column(name = "last_name", nullable = false)
  @NotNull
  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
  private String lastName;

  @Column(nullable = false, unique = true)
  @Pattern(regexp = EMAIL_PATTERN,
      message = "Email is invalid")
  @NotNull
  private String email;

  @Column(name = "job_title", nullable = false)
  @NotNull
  @Size(min = 1, max = DATABASE_STRINGS_MAX_SIZE)
  private String jobTitle;

  @ManyToOne
  @JoinColumn(name = "department_id")
  @NotNull
  private Department department;

  public User(final UserDTO userDTO, final Department department) {
    this.firstName = userDTO.firstName();
    this.lastName = userDTO.lastName();
    this.email = userDTO.email();
    this.jobTitle = userDTO.jobTitle();
    this.department = department;
  }

  public UserDTO toDTO() {
    return new UserDTO(this.id,
        this.firstName,
        this.lastName,
        this.email,
        this.jobTitle,
        this.department.getId());
  }

  public User update(final UserDTO userDTO, final Department department) {
    this.firstName = userDTO.firstName();
    this.lastName = userDTO.lastName();
    this.email = userDTO.email();
    this.jobTitle = userDTO.jobTitle();
    this.department = department;
    return this;
  }

  public String getFullName() {
    return String.format("%s %s", this.getFirstName(), this.getLastName());
  }

}
