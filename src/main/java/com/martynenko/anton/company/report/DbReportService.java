package com.martynenko.anton.company.report;

import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.report.Report.ReportType;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class DbReportService implements ReportService {

  private ReportRepository repository;

  private UserService userService;

  private ReportGenerator reportGenerator;

  @Autowired
  public DbReportService(final ReportRepository repository,
      final UserService userService,
      final ReportGenerator reportGenerator) {
    this.repository = repository;
    this.userService = userService;
    this.reportGenerator = reportGenerator;
  }

  @Override
  @Scheduled(cron = "0 0 0 1 * ?") //every month 1st at the 00:00
  public void generateReports() {
    log.info("Report generation process started");
    generateWorkloadReport();
    generateAvailabilityReport();
  }

  @Override
  public Report getLast(final ReportType reportType) {
    log.debug("Requested last report with type {}", reportType);
    return repository.findFirstByReportTypeOrderByCreationDateDesc(reportType).orElseThrow(
        () -> new EntityNotFoundException(reportType + "")
    );
  }

  protected void generateWorkloadReport() {
    Collection<User> users =  userService.listAll();
    Map<String, List<String[]>> content = users.stream()
        .map(this::collectWorkloadData)
        .collect(Collectors.groupingBy(array -> array[1]));

    byte[] bytes = reportGenerator.generate(content);
    log.info("Saving new workload report...");
    repository.save(new Report(ReportType.WORKLOAD, bytes));
  }

  protected void generateAvailabilityReport() {
    Collection<User> users = userService.listAvailable(30);

    List<String[]> content = users.stream()
        .map(this::collectAvailabilityData).toList();

    byte[] bytes = reportGenerator.generate(
        Map.of("Availability " + LocalDate.now().getMonth(), content)
    );
    log.info("Saving new availability report...");
    repository.save(new Report(ReportType.AVAILABILITY, bytes));
  }

  protected String[] collectAvailabilityData(final User user) {
    String[] array = new String[4];
    array[0] = user.getFullName();
    array[1] = user.getDepartment().getTitle();
    ProjectPosition projectPosition = user.getProjectPosition();
    array[2] = projectPosition == null ? "" : projectPosition.getProject().getTitle();

    String endDate = "";
    if (projectPosition != null) {
      LocalDate positionEndDate = projectPosition.getPositionEndDate();
      if (positionEndDate != null) {
        endDate = positionEndDate.toString();
      }
    }
    array[3] = endDate;
    log.debug("For user {} collected data array {}", user, Arrays.toString(array));
    return array;
  }

  protected String[] collectWorkloadData(final User user) {
    String[] array = new String[4];
    array[0] = user.getFullName();
    array[1] = user.getDepartment().getTitle();

    ProjectPosition projectPosition = user.getProjectPosition();
    array[2] = projectPosition == null ? "" : projectPosition.getProject().getTitle();
    array[3] = projectPosition == null ? "" : projectPosition.getOccupation();
    log.debug("For user {} collected data array {}", user, Arrays.toString(array));
    return array;
  }
}
