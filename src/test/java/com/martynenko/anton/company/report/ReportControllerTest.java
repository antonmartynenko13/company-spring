package com.martynenko.anton.company.report;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martynenko.anton.company.csv.CsvHelper;
import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentDTO;
import com.martynenko.anton.company.department.DepartmentRepository;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.project.ProjectDTO;
import com.martynenko.anton.company.project.ProjectRepository;
import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.projectposition.ProjectPositionDTO;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import com.martynenko.anton.company.report.Report.ReportType;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserDTO;
import com.martynenko.anton.company.user.UserRepository;
import com.martynenko.anton.company.utils.Constants;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //disable security
class ReportControllerTest {

  final String contextPath = "/api/reports/";

  @Autowired
  ProjectPositionRepository projectPositionRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DepartmentRepository departmentRepository;

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  ReportService reportService;


  @Autowired
  MockMvc mockMvc;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void onGetLastWithEmptyBaseShouldReturnNotFound() {

    Arrays.stream(ReportType.values()).forEach(reportType -> {
      try {
        this.mockMvc.perform(get(contextPath + "last")
                .param("reportType", reportType.name()))
            .andDo(print())
            .andExpect(status().isNotFound());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Test
  @Transactional
  void onGetLastShouldReturnOkWithXlsxFileAndContentType() throws Exception {
    importTestDataFromCsv();
    reportService.generateReports();

    Arrays.stream(ReportType.values()).forEach(reportType -> {
      try {
        this.mockMvc.perform(get(contextPath + "last")
                .param("reportType", reportType.name()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(Constants.XLSX_CONTENT_TYPE))
            .andExpect(header().string("Content-Disposition",
            String.format("attachment; filename=\"%s %s.xlsx\"", reportType.name(), LocalDate.now().getMonth())));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  void importTestDataFromCsv() throws FileNotFoundException {
    CsvHelper<DepartmentDTO> departmentDTOCsvHelper = new CsvHelper<>();
    CsvHelper<ProjectDTO> projectDTOCsvHelper = new CsvHelper<>();
    CsvHelper<UserDTO> userDTOCsvHelper = new CsvHelper<>();
    CsvHelper<ProjectPositionDTO> projectPositionDTOCsvHelper = new CsvHelper<>();

    Collection<DepartmentDTO> departmentDTOS = departmentDTOCsvHelper.readAll(new FileInputStream(
        ResourceUtils.getFile("classpath:csv/departments.csv")), DepartmentDTO.class);
    Collection<ProjectDTO> projectDTOS = projectDTOCsvHelper.readAll(new FileInputStream(ResourceUtils.getFile("classpath:csv/projects.csv")), ProjectDTO.class);
    Collection<UserDTO> userDTOS = userDTOCsvHelper.readAll(new FileInputStream(ResourceUtils.getFile("classpath:csv/users.csv")), UserDTO.class);
    Collection<ProjectPositionDTO> projectPositionDTOS = projectPositionDTOCsvHelper.readAll(new FileInputStream(ResourceUtils.getFile("classpath:csv/project_positions.csv")), ProjectPositionDTO.class);

    Collection<Department> departments = departmentDTOS.stream().map(DepartmentDTO::createInstance).toList();
    List<Department> departmentsList = departmentRepository.saveAll(departments);


    Collection<Project> projects = projectDTOS.stream().map(ProjectDTO::createInstance).toList();
    List<Project> projectList = projectRepository.saveAll(projects);

    Collection<User> users = userDTOS.stream().map(userDTO -> userDTO.createInstance(departmentsList.get(userDTO.departmentId().intValue() - 1))).toList();
    List<User> userList = userRepository.saveAll(users);

    Collection<ProjectPosition> projectPositions
        = projectPositionDTOS.stream()
        .map(projectPositionDTO
            -> projectPositionDTO.createInstance(userList.get(projectPositionDTO.userId().intValue() - 1),
            projectList.get(projectPositionDTO.projectId().intValue() - 1))).toList();
    projectPositionRepository.saveAll(projectPositions);
  }
}