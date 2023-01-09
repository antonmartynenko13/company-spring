package com.martynenko.anton.company.report;

import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import com.martynenko.anton.company.report.Report.ReportType;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserService;
import com.martynenko.anton.company.utils.DateInterval;
import com.martynenko.anton.company.utils.ScheduleHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

  private ReportRepository repository;

  private UserService userService;

  private ReportGenerator reportGenerator;

  private ProjectPositionRepository projectPositionRepository;

  public ReportServiceImpl(ReportRepository repository, UserService userService,
      ReportGenerator reportGenerator, ProjectPositionRepository projectPositionRepository) {
    this.repository = repository;
    this.userService = userService;
    this.reportGenerator = reportGenerator;
    this.projectPositionRepository = projectPositionRepository;
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
    return repository.findFirstByReportTypeOrderByIdDesc(reportType).orElseThrow(
        () -> new EntityNotFoundException(reportType + "")
    );
  }

  protected void generateWorkloadReport() {
    Collection<User> users =  userService.listAll();
    Map<String, List<String[]>> content = users.stream()
        .flatMap(user -> collectWorkloadData(user).stream())
        .collect(Collectors.groupingBy(array -> array[1]));

    content.values().forEach(sheet -> sheet.add(0, ReportType.WORKLOAD.headers));

    byte[] bytes = reportGenerator.generate(content);
    log.info("Saving new workload report...");
    repository.save(new Report(ReportType.WORKLOAD, bytes));
  }

  protected void generateAvailabilityReport() {
    Collection<User> users = userService.listAll();

    List<String[]> content = users.stream()
        .map(this::mapUserToAvailabilityContentArray)
        .filter(array -> array.length > 0)
        .collect(Collectors.toList()); //Stream.toList returns immutable list

    content.add(0, ReportType.AVAILABILITY.headers);

    byte[] bytes = reportGenerator.generate(
        Map.of("Availability " + LocalDate.now().getMonth(), content)
    );
    log.info("Saving new availability report...");
    repository.save(new Report(ReportType.AVAILABILITY, bytes));
  }

  private String[] mapUserToAvailabilityContentArray(final User user) {

    List<ProjectPosition> currentPositions = projectPositionRepository.findCurrentPositionsByUser(user);
    if (currentPositions.isEmpty()) {
      return collectAvailabilityData(user, null);
    }

    ProjectPosition latestEndPosition = getProjectPositionWithLatestEndData(currentPositions);

    LocalDate periodEnds = LocalDate.now().plusDays(30);

    if (latestEndPosition.getPositionEndDate() != null
        && latestEndPosition.getPositionEndDate().isBefore(periodEnds)) {
      return collectAvailabilityData(user, latestEndPosition);
    }

    return new String[]{};
  }

  private ProjectPosition getProjectPositionWithLatestEndData(List<ProjectPosition> projectPositions) {
    projectPositions = projectPositions.stream()
        .sorted((this::compareProjectPositionsWithNullableEndDate))
        .toList();

    return projectPositions.get(projectPositions.size() - 1);
  }

  private int compareProjectPositionsWithNullableEndDate(final ProjectPosition p1, final ProjectPosition p2) {
    // null in position end date means that date is unknown and may be as far away as possible.
    if (p1.getPositionEndDate() == null) return 1;
    if (p2.getPositionEndDate() == null) return -1;

    return p1.getPositionEndDate().compareTo(p2.getPositionEndDate());
  }

  private String[] collectAvailabilityData(final User user, final ProjectPosition projectPosition) {
    String[] array = new String[4];
    array[0] = user.getFullName();
    array[1] = user.getDepartment().getTitle();
    array[2] = "";
    array[3] = "";

    if (projectPosition != null) {
      array[2] = projectPosition.getProject().getTitle();
      array[3] = projectPosition.getPositionEndDate().toString();
    }
    log.debug("For user {} collected data array {}", user, array);
    return array;
  }

  private List<String[]> collectWorkloadData(final User user) {
    Collection<ProjectPosition> projectPositions = projectPositionRepository.findCurrentPositionsByUser(user);
    List<String[]> rows = new ArrayList<>();

    if (projectPositions.isEmpty()) {
      rows.add(mapUserToContentArray(user, null));
    } else {
      projectPositions.forEach(projectPosition -> rows.add(mapUserToContentArray(user, projectPosition)));
    }

    log.debug("For user {} collected data array list {}", user, rows);
    return rows;
  }

  private String[] mapUserToContentArray(final User user, final ProjectPosition projectPosition) {
    String[] array = new String[4];
    array[0] = user.getFullName();
    array[1] = user.getDepartment().getTitle();

    array[2] = projectPosition == null ? "" : projectPosition.getProject().getTitle();
    array[3] = projectPosition == null ? "" : projectPosition.getOccupation();
    return array;
  }
}
