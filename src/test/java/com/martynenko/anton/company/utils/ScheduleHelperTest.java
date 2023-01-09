package com.martynenko.anton.company.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.martynenko.anton.company.projectposition.ProjectPosition;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScheduleHelperTest {
  private ScheduleHelper scheduleHelper = new ScheduleHelper();

  @Test
  void whenTargetAndIntervalsNotOverlappingShouldReturnTarget() {
    ProjectPosition projectPosition1 = mock(ProjectPosition.class);
    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-01"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-10"));
    List<ProjectPosition> projectPositions = Collections.singletonList(projectPosition1);

    DateInterval target =  new DateInterval(LocalDate.parse("1999-05-01"), LocalDate.parse("1999-05-10"));
    Collection<DateInterval> availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows).hasSize(1).contains(target);
  }

  @Test
  void shouldReturnCorrectAvailabilityWindows() {

    ProjectPosition projectPosition1 = mock(ProjectPosition.class);
    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-10"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-20"));
    List<ProjectPosition> projectPositions = Collections.singletonList(projectPosition1);

    DateInterval target =  new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-30"));
    Collection<DateInterval> availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows)
        .hasSize(2)
        .contains(new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-10")))
        .contains(new DateInterval(LocalDate.parse("2000-05-20"), LocalDate.parse("2000-05-30")));

    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-10"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-30"));

    target =  new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-30"));
    availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows)
        .hasSize(1)
        .contains(new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-10")));


    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-10"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-30"));

    target =  new DateInterval(LocalDate.parse("2000-05-20"), LocalDate.parse("2000-06-30"));
    availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows)
        .hasSize(1)
        .contains(new DateInterval(LocalDate.parse("2000-05-30"), LocalDate.parse("2000-06-30")));


    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-10"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-30"));

    target =  new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-01"));
    availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows)
        .hasSize(1)
        .contains(new DateInterval(LocalDate.parse("2000-05-01"), LocalDate.parse("2000-05-01")));

    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-10"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-30"));

    target =  new DateInterval(LocalDate.parse("2000-06-01"), LocalDate.parse("2000-06-01"));
    availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows)
        .hasSize(1)
        .contains(new DateInterval(LocalDate.parse("2000-06-01"), LocalDate.parse("2000-06-01")));
  }

  @Test
  void shouldReturnEmptyAvailabilityWindowsCollection() {
    ProjectPosition projectPosition1 = mock(ProjectPosition.class);
    when(projectPosition1.getPositionStartDate()).thenReturn(LocalDate.parse("2000-05-01"));
    when(projectPosition1.getPositionEndDate()).thenReturn(LocalDate.parse("2000-05-30"));
    List<ProjectPosition> projectPositions = Collections.singletonList(projectPosition1);


    DateInterval target =  new DateInterval(LocalDate.parse("2000-05-05"), LocalDate.parse("2000-05-10"));
    Collection<DateInterval> availabilityWindows = scheduleHelper.getAvailabilityWindows(projectPositions, target);
    assertThat(availabilityWindows).isEmpty();
  }
}