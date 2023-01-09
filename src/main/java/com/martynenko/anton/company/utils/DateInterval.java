package com.martynenko.anton.company.utils;

import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class DateInterval {

  private final LocalDate start;
  private final LocalDate end;


  public DateInterval(LocalDate start, LocalDate end) {
    if (start == null) start = LocalDate.MIN;
    if (end == null) end = LocalDate.MAX;

    if (start.isAfter(end)) throw new IllegalStateException("Start date must be less than end date");
    this.start = start;
    this.end = end;
  }

  public LocalDate getStart() {
    return start;
  }

  public LocalDate getEnd() {
    return end;
  }

  public boolean includes(LocalDate localDate) {
    return (localDate.isAfter(start) || localDate.isEqual(start))
        && (localDate.isBefore(end) || localDate.isEqual(end));
  }

  public boolean includes(DateInterval dateInterval) {
    return this.includes(dateInterval.getStart()) && this.includes(dateInterval.getEnd());
  }

  public boolean contains(LocalDate localDate) {
    return (localDate.isAfter(start))
        && (localDate.isBefore(end));
  }
}
