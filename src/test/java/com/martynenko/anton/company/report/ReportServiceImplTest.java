package com.martynenko.anton.company.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import com.martynenko.anton.company.report.Report.ReportType;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

class ReportServiceImplTest {

  UserService userService = mock(UserService.class);

  ReportRepository reportRepository = mock(ReportRepository.class);

  ReportGenerator reportGenerator = mock(ReportGenerator.class);

  ProjectPositionRepository projectPositionRepository = mock(ProjectPositionRepository.class);

  ReportServiceImpl service = new ReportServiceImpl(reportRepository, userService, reportGenerator, projectPositionRepository);

  @Test
  void whenNoUsers_ThenShouldRunSuccessfully() {
    service.generateReports();
    assertTrue(true);
  }

  @Test
  void whenUserHasNoProjectPosition_ThenShouldRunSuccessfully() {
    Department department = mock(Department.class);
    when(department.getTitle()).thenReturn("Department title 1");

    User user = mock(User.class);
    when(user.getFirstName()).thenReturn("Firstname Lastname");
    when(user.getDepartment()).thenReturn(department);
    //user has no position
   // when(user.getProjectPosition()).thenReturn(null);

    when(userService.listAll()).thenReturn(List.of(user));

    byte[] generatedReport = new byte[0];

    when(reportGenerator.generate(anyMap())).thenReturn(generatedReport);

    //user has no position
   // when(user.getProjectPosition()).thenReturn(null);

    service.generateReports();
    assertTrue(true);
  }

  @Test
  void whenUserHasProjectPosition_ThenShouldRunSuccessfully() {
    long days = 30;
    Department department = mock(Department.class);
    when(department.getTitle()).thenReturn("Department title 1");

    User user = mock(User.class);
    when(user.getFirstName()).thenReturn("Firstname Lastname");
    when(user.getDepartment()).thenReturn(department);

    Project project = mock(Project.class);
    when(project.getTitle()).thenReturn("Project 1");

    ProjectPosition projectPosition = mock(ProjectPosition.class);
    when(projectPosition.getProject()).thenReturn(project);

    //when(user.getProjectPosition()).thenReturn(projectPosition);

    when(userService.listAll()).thenReturn(List.of(user));
    //when(userService.listAvailable(days)).thenReturn(List.of(user));

    byte[] generatedReport = new byte[0];

    when(reportGenerator.generate(anyMap())).thenReturn(generatedReport);

    //if position end date is unknown
    when(projectPosition.getPositionEndDate()).thenReturn(null);

    service.generateReports();

    //if position is known
    when(projectPosition.getPositionEndDate()).thenReturn(LocalDate.now());

    service.generateReports();
    assertTrue(true);
  }

  @Test
  void shouldReturnReport() {
    Report report = mock(Report.class);

    when(reportRepository
        .findFirstByReportTypeOrderByIdDesc(any(ReportType.class)))
        .thenReturn(Optional.of(report));

    Arrays.stream(ReportType.values()).forEach( reportType -> {
      assertThat(service.getLast(reportType)).isEqualTo(report);
    });
  }

  @Test
  void shouldThrowNoEntityException() {

    when(reportRepository
        .findFirstByReportTypeOrderByIdDesc(any(ReportType.class)))
        .thenReturn(Optional.ofNullable(null));

    Arrays.stream(ReportType.values()).forEach( reportType -> {
      assertThrows(EntityNotFoundException.class, () -> {
        service.getLast(reportType);
      });
    });
  }

}