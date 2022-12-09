package com.martynenko.anton.company.department;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //disable security
@Transactional
class DepartmentControllerTest {
  final String contextPath = "/api/departments/";

  final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  MockMvc mockMvc;

  @Autowired
  DepartmentRepository departmentRepository;

  @MockBean
  private JwtDecoder jwtDecoder;


  @Test
  void onCreateShouldReturnCreatedWithLocationHeaderOn() throws Exception {
      Map<String, String> payloadMap = Map.of("title", "Department1");


      this.mockMvc.perform(post(contextPath)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.mapper.writeValueAsString(payloadMap)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(redirectedUrlPattern(contextPath + "*"));
  }

  @Test
  void onCreateWithDuplicationShouldReturnConflict() throws Exception {
    departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    //duplication of unique title
    Map<String, String> payloadMap = Map.of("title", "Department1");

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateShouldReturnOkWithSameEntity() throws Exception {
    long id = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance()).getId();

    Map<String, String> payloadMap
        = Map.of( "title", "Department2");
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
    long id = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance()).getId();
    departmentRepository.save(new DepartmentDTO(null,"Department2").createInstance());

    //duplication of unique title
    Map<String, String> payloadMap = Map.of("title", "Department2");

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

    //clean up after yourself
    departmentRepository.deleteAll();
  }

  @Test
  void onUpdateWithMissingIdShouldReturnNotFound() throws Exception {
    long missingId = 0;
    Map<String, String> payloadMap
        = Map.of("title", "Department0");

    this.mockMvc.perform(put(contextPath + missingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }


  @Test
  void onGetShouldReturnOkWithSingleEntity() throws Exception {
    long id = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance()).getId();

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
    long id = departmentRepository.save(new DepartmentDTO(1L,"Department1").createInstance()).getId();
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
    departmentRepository.save(new DepartmentDTO(1L,"Department1").createInstance());

      this.mockMvc.perform(get(contextPath))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
          .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
  }
}