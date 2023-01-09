package com.martynenko.anton.company.utils;

import com.martynenko.anton.company.projectposition.ProjectPosition;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduleHelper {

  public List<DateInterval> getAvailabilityWindows(final Collection<ProjectPosition> projectPositions,
      final DateInterval targetPeriod) {

    List<DateInterval> projectPositionIntervals = getProjectPositionsIntervals(projectPositions);

    List<DateInterval> mergedIntervals = merge(projectPositionIntervals);

    return getAvailabilityWindows(targetPeriod, mergedIntervals);
  }

  private List<DateInterval> merge(List<DateInterval> intervals) {

    if(intervals.size() <= 1)
      return intervals;

    Collections.sort(intervals, Comparator.comparing(DateInterval::getStart));

    DateInterval first = intervals.get(0);
    LocalDate start = first.getStart();
    LocalDate end = first.getEnd();

    ArrayList<DateInterval> result = new ArrayList<>();

    for (int i = 1; i < intervals.size(); i++) {
      DateInterval current = intervals.get(i);
      if (current.getStart().isBefore(end) || current.getStart().isEqual(end)) {
        end = Stream.of(current.getEnd(), end).max(LocalDate::compareTo).orElse(current.getEnd());
      } else {
        result.add(new DateInterval(start, end));
        start = current.getStart();
        end = current.getEnd();
      }
    }

    result.add(new DateInterval(start, end));
    return result;
  }

  private List<DateInterval> getAvailabilityWindows(final DateInterval target,
      final List<DateInterval> projectPositionIntervals) {
    log.info(projectPositionIntervals.toString());
    Collections.sort(projectPositionIntervals, Comparator.comparing(DateInterval::getStart));
    SortedSet<DateInterval> result = new TreeSet<>(Comparator.comparing(DateInterval::getStart));

    //if there is no working periods, full target period is free
    if (projectPositionIntervals.isEmpty()) {
      result.add(target);
      return new ArrayList<>(result);
    }

    LocalDate windowStart = target.getStart();
    LocalDate windowEnd = target.getEnd();

    for (DateInterval interval: projectPositionIntervals) {
      //if interval include full target interval there no free windows
      if (interval.includes(target)) return Collections.EMPTY_LIST;

      if (target.contains(interval.getStart())) {
        windowEnd = interval.getStart();
        result.add(new DateInterval(windowStart, windowEnd));
        windowEnd = target.getEnd();
      }
      if (target.contains(interval.getEnd())) {
        windowStart = interval.getEnd();
      }
    }

    result.add(new DateInterval(windowStart, windowEnd));
    return new ArrayList<>(result);
  }

  private List<DateInterval> getProjectPositionsIntervals(final Collection<ProjectPosition> projectPositions) {
    return projectPositions.stream()
        .map(projectPosition -> new DateInterval(projectPosition.getPositionStartDate(),
            projectPosition.getPositionEndDate()))
        .collect(Collectors.toList()); //Stream.toList() returns immutable list
  }
}
