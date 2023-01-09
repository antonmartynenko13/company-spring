package com.martynenko.anton.company.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ReportsApiSecurityTest {

  String contextPath = "/api/reports/";

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void onUnauthorizedGetLastReportShouldReturnUnauthorized() throws Exception {
    this.mockMvc.perform(get(contextPath + "last"))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onAuthorizedGetLastReportShouldReturnNotUnauthorized() throws Exception {
    assertThat(this.mockMvc.perform(get(contextPath + "last").with(jwt()))
        .andDo(print()).andReturn().getResponse().getStatus())
        .isNotEqualTo(HttpStatus.UNAUTHORIZED.value());
  }
}
