package com.martynenko.anton.company.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentDTO;
import com.martynenko.anton.company.department.DepartmentRepository;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.project.ProjectDTO;
import com.martynenko.anton.company.project.ProjectRepository;
import com.martynenko.anton.company.projectposition.ProjectPositionDTO;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DepartmentRepository departmentRepository;
  @Autowired
  private ProjectPositionRepository projectPositionRepository;
  @Autowired
  private ProjectRepository projectRepository;

  @Test
  //@Transactional
  void findAllByProjectPositionStartDateNotLessThanDate() {
    LocalDate userIsBusyFrom = LocalDate.now().plusDays(2);
    LocalDate userIsBusyTo = LocalDate.now().plusDays(7);
    Project project
        = projectRepository.save(new ProjectDTO(
            null,
        "Project1",
        userIsBusyFrom,
        userIsBusyTo)
        .createInstance());

    Department department
        = departmentRepository.save(new DepartmentDTO(
            null,
        "Department"
    ).createInstance());

    User user = userRepository.save(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));

    projectPositionRepository.save(
        new ProjectPositionDTO(
            null,
            user.getId(),
            project.getId(),
            userIsBusyFrom,
            userIsBusyTo,
            "Some title",
            "Some occupation"
        ).createInstance(user, project)
    );

    assertThat(userRepository.findAllWithoutCurrentProjectPosition()).hasSize(1);

    //user has no project
    userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email2@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));

    assertThat(userRepository.findAllWithoutCurrentProjectPosition()).hasSize(2);
  }
}