package com.martynenko.anton.company.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class CsvHelper<T> {

  /*
  * Reads all dto entities from input stream
  */

  public Collection<T> readAll(final InputStream inputStream, final Class<T> dtoType) {
    CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
    CsvMapper mapper = new CsvMapper();
    mapper.findAndRegisterModules(); //for java 8 types compatibility
    try (BufferedReader fileReader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      MappingIterator<T> readValues
          = mapper.readerFor(dtoType).with(bootstrapSchema).readValues(fileReader);
      return readValues.readAll();

    } catch (IOException e) {
      log.error(e.toString());
      throw new UncheckedIOException(e);
    }
  }

  /*
   * Reads all dto entities from multipart csv
   */

  public Collection<T> readAll(final MultipartFile file, final Class<T> dtoType) {
    try {
      Collection<T> dtos = readAll(file.getInputStream(), dtoType);
      log.info("Read {} dto from file", dtos.size());
      return dtos;
    } catch (IOException e) {
      log.error(e.toString());
      throw new UncheckedIOException(e);
    }
  }
}
