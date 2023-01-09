package com.martynenko.anton.company.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CrudApisWebSecurityTest {
  static List<String> crudContexts = List.of(
      "/api/departments/",
      "/api/projects.csv/",
      "/api/users/",
      "/api/project-positions/"
  );

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private JwtDecoder jwtDecoder;


  @Test
  void onUnauthorizedRequestsShouldReturnUnauthorized()
      throws Exception {
    for (String contextPath: crudContexts){

      this.mockMvc.perform(get(contextPath ))
          .andDo(print())
          .andExpect(status().isUnauthorized())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

      this.mockMvc.perform(post(contextPath))
          .andDo(print())
          .andExpect(status().isUnauthorized())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

      this.mockMvc.perform(put(contextPath))
          .andDo(print())
          .andExpect(status().isUnauthorized())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

      this.mockMvc.perform(delete(contextPath))
          .andDo(print())
          .andExpect(status().isUnauthorized())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }
  }

  @Test
  void onAuthorizedRequestsShouldNotReturnUnauthorized() throws Exception {

    for (String contextPath: crudContexts){
      assertThat(this.mockMvc.perform(get(contextPath).with(jwt()))
          .andDo(print()).andReturn().getResponse()
          .getStatus()).isNotEqualTo(HttpStatus.UNAUTHORIZED.value());

      assertThat(this.mockMvc.perform(post(contextPath).with(jwt()))
          .andDo(print()).andReturn().getResponse()
          .getStatus()).isNotEqualTo(HttpStatus.UNAUTHORIZED.value());

      assertThat(this.mockMvc.perform(put(contextPath).with(jwt()))
          .andDo(print()).andReturn().getResponse()
          .getStatus()).isNotEqualTo(HttpStatus.UNAUTHORIZED.value());

      assertThat(this.mockMvc.perform(delete(contextPath).with(jwt()))
          .andDo(print()).andReturn().getResponse()
          .getStatus()).isNotEqualTo(HttpStatus.FORBIDDEN.value());
    }
  }
}
