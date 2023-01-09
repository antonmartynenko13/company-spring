package com.martynenko.anton.company.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Builder
@ToString
@Slf4j
public class XlsxReportGenerator implements ReportGenerator {
  @Builder.Default private final String headerFontName = "Arial";

  @Builder.Default private final short headerFontHeight = 14;

  @Builder.Default private final boolean headerFontIsBold = false;

  @Builder.Default private final String contentFontName = "Arial";

  @Builder.Default private final short contentFontHeight = 12;

  @Builder.Default private final boolean contentFontIsBold = false;

  /**
   * The content submitted for report generation organized in Map
   * Every kay in map should be converted into nested sheet (may match department for example)
   * Every value is a List of sheet's row, organized in array
   * list.get(0) always contains header values array
   * */

  @Override
  public byte[] generate(final Map<String, List<String[]>> contentBySheets) {
    log.info("Generating new xlsx report...");
    if (contentBySheets == null) {
      throw new IllegalStateException("Content is required");
    }

    try (Workbook workbook = new XSSFWorkbook()) {

      //Process nested sheets step by step
      for (String sheetName : contentBySheets.keySet()) {
        Sheet sheet = workbook.createSheet(sheetName);

        List<String[]> content = contentBySheets.get(sheetName);

        String[] headerArray = content.get(0);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = getCellStyle(workbook, true);

        //Fill header's cells
        for (int i = 0; i < headerArray.length; i++) {
          Cell headerCell = header.createCell(i);
          headerCell.setCellValue(headerArray[i]);
          headerCell.setCellStyle(headerStyle);
        }

        CellStyle contentStyle = getCellStyle(workbook, false);

        //Fill content cells, row by row
        for (int i = 1; i < content.size(); i++) {
          Row row = sheet.createRow(i);

          for (int j = 0; j < content.get(i).length; j++) {
            Cell cell = row.createCell(j);
            cell.setCellValue(content.get(i)[j]);
            cell.setCellStyle(contentStyle);
          }
        }
        for (int i = 0; i < content.get(0).length; i++) {
          sheet.autoSizeColumn(i);
        }

      }


      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      workbook.write(baos);
      return baos.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private CellStyle getCellStyle(final Workbook workbook, final boolean isHeader) {
    XSSFFont font = ((XSSFWorkbook) workbook).createFont();
    font.setFontName(isHeader ? this.headerFontName : this.contentFontName);
    font.setFontHeightInPoints(isHeader ? this.headerFontHeight : this.contentFontHeight);
    font.setBold(isHeader ? this.headerFontIsBold : this.contentFontIsBold);

    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(font);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    return cellStyle;
  }
}
