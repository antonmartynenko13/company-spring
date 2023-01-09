package com.martynenko.anton.company.projectposition;

import static org.assertj.core.api.Assertions.assertThat;

import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentDTO;
import com.martynenko.anton.company.department.DepartmentRepository;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.project.ProjectDTO;
import com.martynenko.anton.company.project.ProjectRepository;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserDTO;
import com.martynenko.anton.company.user.UserRepository;
import java.time.LocalDate;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProjectPositionRepositoryTest {

  @Autowired
  private ProjectPositionRepository projectPositionRepository;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void findCurrentPositionsByUser() {
    LocalDate projectStart = LocalDate.now().minusDays(5);
    LocalDate projectEnd = LocalDate.now().plusDays(5);

    User user = getUser();
    Project project = projectRepository.save(new ProjectDTO(null,"Project", projectStart, projectEnd)
        .createInstance());

    long id = projectPositionRepository.save(
        new ProjectPositionDTO(
            null,
            user.getId(),
            project.getId(),
            projectStart,
            projectEnd,
            "Some title",
            "Some occupation"
        ).createInstance(user, project)
    ).getId();

    Collection<ProjectPosition> positions = projectPositionRepository.findCurrentPositionsByUser(user);
    System.out.println(positions);
    assertThat(positions).hasSize(1);
  }

  User getUser() {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department").createInstance());

    return userRepository.save(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));
  }
}