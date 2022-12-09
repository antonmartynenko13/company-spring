package com.martynenko.anton.company.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martynenko.anton.company.department.DepartmentDTO;
import java.time.LocalDate;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //disable security
@Transactional
class ProjectControllerTest {

  final String contextPath = "/api/projects/";

  final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Autowired
  ProjectRepository projectRepository;

  @Test
  void onCreateShouldReturnCreatedWithLocationHeaderOn() throws Exception {
    Map<String, String> payloadMap = Map.of("title", "Project1",
                                            "startDate", "2018-07-22",
                                            "endDate", "2019-07-22"
                                            );


    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern(contextPath + "*"));
  }

  @Test
  void onCreateWithDuplicationShouldReturnConflict() throws Exception {
    projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now()).createInstance());

    //duplication of unique title
    Map<String, String> payloadMap = Map.of("title", "Project1",
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onCreateWithEmptyMandatoryFieldShouldReturnBadRequest() throws Exception {
    projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now()).createInstance());

    //duplication of unique title
    Map<String, String> payloadMap = Map.of(
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateShouldReturnOkWithSameEntity() throws Exception {
    long id = projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now())
        .createInstance()).getId();

    Map<String, String> payloadMap = Map.of(
        "title", "Project1",
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
    );
    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
  }

  /*
   * Need this workaround to prevent update with duplicate value inside transaction
   * And I still want to use declaration initialization, so @DirtiesContext will crush context
   * */

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void onUpdateWithDuplicationShouldReturnConflict() throws Exception {
    long id = projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now())
        .createInstance()).getId();
    projectRepository.save(new ProjectDTO(null,"Project2", LocalDate.now(), LocalDate.now())
        .createInstance());

    //duplication of unique title

    Map<String, String> payloadMap = Map.of(
        "title", "Project2",
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

    //clean up
    projectRepository.deleteAll();
  }

  @Test
  void onUpdateWithoutMandatoryFieldShouldReturnBadRequest() throws Exception {
    long id = projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now())
        .createInstance()).getId();
    projectRepository.save(new ProjectDTO(null,"Project2", LocalDate.now(), LocalDate.now())
        .createInstance());

    Map<String, String> payloadMap = Map.of(
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateWithMissingIdShouldReturnNotFound() throws Exception {
    long missingId = 0;
    Map<String, String> payloadMap = Map.of(
        "title", "Project2",
        "startDate", "2018-07-22",
        "endDate", "2019-07-22"
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
    long id = projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now())
        .createInstance()).getId();

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
    long id = projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now())
        .createInstance()).getId();

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
    projectRepository.save(new ProjectDTO(null,"Project1", LocalDate.now(), LocalDate.now()).createInstance());

    this.mockMvc.perform(get(contextPath))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
  }
}