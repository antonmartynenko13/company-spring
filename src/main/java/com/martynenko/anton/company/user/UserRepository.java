package com.martynenko.anton.company.user;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = """
      SELECT * 
      FROM users u
      WHERE u.id NOT IN (
        SELECT DISTINCT pp.user_id
        FROM project_position pp
        WHERE pp.position_start_date <= CURRENT_DATE
        AND ( pp.position_end_date IS NULL OR pp.position_end_date > CURRENT_DATE )
      ) ;
      """, nativeQuery = true)
  Collection<User> findAllWithoutCurrentProjectPosition();
}
