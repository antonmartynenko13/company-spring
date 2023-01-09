package com.martynenko.anton.company.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateIntervalTest {
  @Test
  void whenIntervalIncludesDateShouldReturnTrue() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertTrue(dateInterval.includes(LocalDate.parse("2000-12-05")));
    assertTrue(dateInterval.includes(LocalDate.parse("2000-12-01")));
    assertTrue(dateInterval.includes(LocalDate.parse("2000-12-10")));
  }

  @Test
  void whenIntervalDoesNotIncludesDateShouldReturnFalse() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertFalse(dateInterval.includes(LocalDate.parse("1999-12-05")));
    assertFalse(dateInterval.includes(LocalDate.parse("2022-12-10")));
  }

  @Test
  void whenIntervalIncludesIntervalShouldReturnTrue() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertTrue(dateInterval.includes(new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"))));
    assertTrue(dateInterval.includes(new DateInterval(LocalDate.parse("2000-12-02"), LocalDate.parse("2000-12-09"))));
  }

  @Test
  void whenIntervalDoesNotIncludesIntervalShouldReturnFalse() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertFalse(dateInterval.includes(new DateInterval(LocalDate.parse("2000-11-01"), LocalDate.parse("2000-12-09"))));
    assertFalse(dateInterval.includes(new DateInterval(LocalDate.parse("2000-12-05"), LocalDate.parse("2000-12-15"))));
    assertFalse(dateInterval.includes(new DateInterval(LocalDate.parse("1999-11-01"), LocalDate.parse("1999-12-09"))));
    assertFalse(dateInterval.includes(new DateInterval(LocalDate.parse("2022-12-02"), LocalDate.parse("2022-12-09"))));
  }

  @Test
  void whenIntervalContainsDateShouldReturnTrue() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertTrue(dateInterval.contains(LocalDate.parse("2000-12-05")));
    assertTrue(dateInterval.contains(LocalDate.parse("2000-12-02")));
    assertTrue(dateInterval.contains(LocalDate.parse("2000-12-09")));
  }

  @Test
  void whenIntervalDoesNotContainsDateShouldReturnFalse() {
    DateInterval dateInterval
        = new DateInterval(LocalDate.parse("2000-12-01"), LocalDate.parse("2000-12-10"));
    assertFalse(dateInterval.contains(LocalDate.parse("2000-12-01")));
    assertFalse(dateInterval.contains(LocalDate.parse("2000-12-10")));
    assertFalse(dateInterval.contains(LocalDate.parse("1999-12-02")));
    assertFalse(dateInterval.contains(LocalDate.parse("2022-12-09")));
  }

  @Test
  void whenCreatedWithNullParametersShouldFillwithMinAndMaxDate() {
    DateInterval dateInterval = new DateInterval(null, null);
    assertThat(dateInterval.getStart()).isEqualTo(LocalDate.MIN);
    assertThat(dateInterval.getEnd()).isEqualTo(LocalDate.MAX);
  }

  @Test
  void whenCreatedWithWrongParamsShouldThrowIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> new DateInterval(LocalDate.MAX, LocalDate.MIN));
  }
}