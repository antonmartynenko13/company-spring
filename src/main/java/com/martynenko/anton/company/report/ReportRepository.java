package com.martynenko.anton.company.report;

import com.martynenko.anton.company.report.Report.ReportType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findFirstByReportTypeOrderByCreationDateDesc(ReportType reportType);
}
