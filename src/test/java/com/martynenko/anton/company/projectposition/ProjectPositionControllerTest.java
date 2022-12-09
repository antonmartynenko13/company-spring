package com.martynenko.anton.company.projectposition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //disable security
@Transactional
class ProjectPositionControllerTest {
  final String contextPath = "/api/project-positions/";

  final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Autowired
  ProjectPositionRepository projectPositionRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DepartmentRepository departmentRepository;

  @Autowired
  ProjectRepository projectRepository;

  @Test
  void onCreateShouldReturnCreatedWithLocationHeaderOn() throws Exception {
    User user = getUser();
    Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
        .createInstance());

    Map<String, String> payloadMap = Map.of(
        "userId", String.valueOf(user.getId()),
        "projectId", String.valueOf(project.getId()),
        "positionStartDate", "2018-07-22",
        "positionEndDate", "2018-10-22",
        "positionTitle", "sometitle",
        "occupation", "Some occupation"
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern(contextPath + "*"));
  }


    @Test
    void onCreateWithoutMandatoryFieldShouldReturnBadRequest() throws Exception {
      User user = getUser();
      Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
          .createInstance());

      Map<String, String> payloadMap = Map.of(
          "userId", String.valueOf(user.getId()),
          "projectId", String.valueOf(project.getId()),
          "positionStartDate", "2018-07-22",
          "positionEndDate", "2018-10-22",
          //no title
          "occupation", "Some occupation"
      );

      this.mockMvc.perform(post(contextPath)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.mapper.writeValueAsString(payloadMap)))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    void onCreateWithoutMandatoryRelationShouldReturnNotFound() throws Exception {

      Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
          .createInstance());

      Map<String, String> payloadMap = Map.of(
          //no such user
          "userId", "100",
          "projectId", String.valueOf(project.getId()),
          "positionStartDate", "2018-07-22",
          "positionEndDate", "2018-10-22",
          "positionTitle", "sometitle",
          "occupation", "Some occupation"
      );

      this.mockMvc.perform(post(contextPath)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.mapper.writeValueAsString(payloadMap)))
          .andDo(print())
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    void onUpdateShouldReturnOkWithSameEntity() throws Exception {
      User user = getUser();
      Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
          .createInstance());

      long id = projectPositionRepository.save(
          new ProjectPositionDTO(
              null,
              user.getId(),
              project.getId(),
              LocalDate.now(),
              LocalDate.now(),
              "Some title",
              "Some occupation"
          ).createInstance(user, project)
      ).getId();

      Map<String, String> payloadMap = Map.of(
          "userId", String.valueOf(user.getId()),
          "projectId", String.valueOf(project.getId()),
          "positionStartDate", "2018-07-22",
          "positionEndDate", "2018-10-22",
          "positionTitle", "sometitle",
          "occupation", "Some occupation"
      );

      this.mockMvc.perform(put(contextPath + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.mapper.writeValueAsString(payloadMap)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
    }



      @Test
      void onUpdateWithoutRequiredFieldShouldReturnBadRequest() throws Exception {
        User user = getUser();
        Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
            .createInstance());

        long id = projectPositionRepository.save(
            new ProjectPositionDTO(
                null,
                user.getId(),
                project.getId(),
                LocalDate.now(),
                LocalDate.now(),
                "Some title",
                "Some occupation"
            ).createInstance(user, project)
        ).getId();

        Map<String, String> payloadMap = Map.of(
            "userId", String.valueOf(user.getId()),
            "projectId", String.valueOf(project.getId()),
            "positionStartDate", "2018-07-22",
            "positionEndDate", "2018-10-22",
            //no title
            "occupation", "Some occupation"
        );

        this.mockMvc.perform(put(contextPath + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(payloadMap)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
      }


      @Test
      void onUpdateWithoutMandatoryRelationShouldReturnNotFound() throws Exception {
        User user = getUser();
        Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
            .createInstance());

        long id = projectPositionRepository.save(
            new ProjectPositionDTO(
                null,
                user.getId(),
                project.getId(),
                LocalDate.now(),
                LocalDate.now(),
                "Some title",
                "Some occupation"
            ).createInstance(user, project)
        ).getId();

        Map<String, String> payloadMap = Map.of(
            "userId", String.valueOf(user.getId()),
            //no such project
            "projectId", "100",
            "positionStartDate", "2018-07-22",
            "positionEndDate", "2018-10-22",
            "positionTitle", "sometitle",
            "occupation", "Some occupation"
        );

        this.mockMvc.perform(put(contextPath + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(payloadMap)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
      }

       @Test
       void onUpdateWithMissingIdShouldReturnNotFound() throws Exception {
         long missingId = 0;
         User user = getUser();
         Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
             .createInstance());

         Map<String, String> payloadMap = Map.of(
             "userId", String.valueOf(user.getId()),
             "projectId", String.valueOf(project.getId()),
             "positionStartDate", "2018-07-22",
             "positionEndDate", "2018-10-22",
             "positionTitle", "sometitle",
             "occupation", "Some occupation"
         );

         this.mockMvc.perform(put(contextPath + missingId)
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(this.mapper.writeValueAsString(payloadMap)))
             .andDo(print())
             .andExpect(status().isNotFound())
             .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
       }

       @Test
       void onGetShouldReturnOkWithSingleEntity() throws Exception {
         User user = getUser();
         Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
             .createInstance());

         long id = projectPositionRepository.save(
             new ProjectPositionDTO(
                 null,
                 user.getId(),
                 project.getId(),
                 LocalDate.now(),
                 LocalDate.now(),
                 "Some title",
                 "Some occupation"
             ).createInstance(user, project)
         ).getId();

         this.mockMvc.perform(get(contextPath + id))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
             .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
       }

       @Test
       void onGetWithMissingIdShouldReturnNotFound() throws Exception {
         long missingId = 0;

         this.mockMvc.perform(get(contextPath + missingId))
             .andDo(print())
             .andExpect(status().isNotFound())
             .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
       }

       @Test
       void onDeleteShouldReturnNoContent() throws Exception {
         User user = getUser();
         Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
             .createInstance());

         long id = projectPositionRepository.save(
             new ProjectPositionDTO(
                 null,
                 user.getId(),
                 project.getId(),
                 LocalDate.now(),
                 LocalDate.now(),
                 "Some title",
                 "Some occupation"
             ).createInstance(user, project)
         ).getId();

         this.mockMvc.perform(delete(contextPath + id))
             .andDo(print())
             .andExpect(status().isNoContent());
       }

       @Test
       void onDeleteWithMissingIdShouldReturnNotFound() throws Exception {
         long missingId = 0;

         this.mockMvc.perform(delete(contextPath + missingId))
             .andDo(print())
             .andExpect(status().isNotFound())
             .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
       }


       @Test
       void onGetAllShouldReturnOkWithJsonListOfEntities() throws Exception {
         User user = getUser();
         Project project = projectRepository.save(new ProjectDTO(null,"Project", LocalDate.now(), LocalDate.now())
             .createInstance());

         long id = projectPositionRepository.save(
             new ProjectPositionDTO(
                 null,
                 user.getId(),
                 project.getId(),
                 LocalDate.now(),
                 LocalDate.now(),
                 "Some title",
                 "Some occupation"
             ).createInstance(user, project)
         ).getId();

         this.mockMvc.perform(get(contextPath))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
             .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
             .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
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