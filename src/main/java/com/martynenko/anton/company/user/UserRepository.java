package com.martynenko.anton.company.user;

import java.time.LocalDate;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  /*
  * Another solution is declaring
  * findAllByProjectPositionIsNullOrProjectPositionPositionStartDateGreaterThanOrProjectPositionPositionEndDateLessThan
  * But this option is much more readable.
  * If user currently available,  periodEndDate may be any
  * */
  @Query("""
      SELECT u 
      FROM User u 
      LEFT JOIN u.projectPosition p 
      WHERE p IS NULL 
      OR p.positionStartDate > :periodStartDate 
      OR p.positionEndDate < :periodEndDate 
      """)
  Collection<User> findAvailable(LocalDate periodStartDate, LocalDate periodEndDate);

}
