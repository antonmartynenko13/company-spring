package com.martynenko.anton.company.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class CsvHelperTest {

  @MockBean
  private JwtDecoder jwtDecoder;

  record DTO(@JsonProperty("value1") @NotBlank String value1,
             @JsonProperty("value2") @NotBlank String value2) {
  }

  MockMultipartFile file
      = new MockMultipartFile(
      "file",
      "file.csv",
      MediaType.TEXT_PLAIN_VALUE,
      "value1, value2\nSome dummy value1,Some dummy value2".getBytes()
  );

  MockMultipartFile mailformedFile
      = new MockMultipartFile(
      "file",
      "file.csv",
      MediaType.TEXT_PLAIN_VALUE,
      "value1, value2\n,Some dummy value2".getBytes()
  );

  @Autowired
  CsvHelper<DTO> csvHelper;

  @Test
  void shouldReturnNotEmptyCollectionOfDto() {
    Collection<DTO> dtos = csvHelper.readAll(file, DTO.class);
    assertThat(dtos).hasSize(1);
    DTO dto = dtos.iterator().next();
    assertThat(dto.value1).isEqualTo("Some dummy value1");
    assertThat(dto.value2).isEqualTo("Some dummy value2");
  }
}