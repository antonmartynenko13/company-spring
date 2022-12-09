package com.martynenko.anton.company.report;

import com.martynenko.anton.company.report.Report.ReportType;

public interface ReportService {
  void generateReports();

  Report getLast(ReportType reportType);
}
