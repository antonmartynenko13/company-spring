package com.martynenko.anton.company.report;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfiguration {
  @Bean
  XlsxReportGenerator reportGenerator() {
    //default building
    //return new XlsxReportGenerator.ReportGeneratorBuilder().build();
    return XlsxReportGenerator.builder().build();
  }

}
