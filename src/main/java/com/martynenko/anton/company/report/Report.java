package com.martynenko.anton.company.report;

import java.time.LocalDateTime;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

@Getter
@NoArgsConstructor
@Entity
public class Report {
  public enum ReportType {
    WORKLOAD("Employee", "Department", "Project", "Occupation"),
    AVAILABILITY("Employee", "Department", "Project", "Project position end date");

    String[] headers;

    ReportType(String... headers) {
      this.headers = headers;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "report_type", nullable = false)
  @Enumerated(value = EnumType.STRING)
  @NotNull
  private ReportType reportType;

  @Column(name = "binary_data")
  @Lob
  @Type(type = "org.hibernate.type.BinaryType")
  @NotEmpty
  private byte[] binaryData;

  @Column(name = "creation_date", insertable = false)
  @CreationTimestamp
  private LocalDateTime creationDate;

  public Report(final ReportType reportType, final byte[] binaryData) {
    this.reportType = reportType;
    this.binaryData = binaryData;
  }
}
