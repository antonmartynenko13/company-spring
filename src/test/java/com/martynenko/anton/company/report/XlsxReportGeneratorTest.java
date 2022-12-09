package com.martynenko.anton.company.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class XlsxReportGeneratorTest {

  @Test
  void shouldReturnValidBinaryDataWithDefaultOptions() {
    XlsxReportGenerator xlsxReportGenerator = new XlsxReportGenerator.ReportGeneratorBuilder().build();

    Map<String, List<String[]>> inputSheetsContent = new LinkedHashMap<>();

    List<String[]> inputContent = new ArrayList<>();
    inputContent.add(new String[]{"Header 1", "Header 2", "Header 3", "Header 4"});
    inputContent.add(new String[]{"Value 1", "Value 2", "Value 3", "Value 4"});

    inputSheetsContent.put("Department 1", inputContent);

    byte[] bytes = xlsxReportGenerator.generate(inputSheetsContent);

    assertThat(bytes).isNotEmpty();

    InputStream is = new ByteArrayInputStream(bytes);
    try(Workbook workbook = WorkbookFactory.create(is)) {

      for (String sheetName: inputSheetsContent.keySet()) {
        Sheet sheet = workbook.getSheet(sheetName);
        assertThat(sheet).isNotNull();
        List<String[]> data = inputSheetsContent.get(sheetName);
        for (int i = 0; i < data.size(); i++) {
          String[] row = data.get(i);
          for (int j = 0; j < row.length; j++) {
            assertThat(sheet.getRow(i).getCell(j)).hasToString(row[j]);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}