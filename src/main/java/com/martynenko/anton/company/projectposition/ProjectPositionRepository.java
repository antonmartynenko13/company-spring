package com.martynenko.anton.company.projectposition;

import com.martynenko.anton.company.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {
  @Query("""
      SELECT pp 
      FROM ProjectPosition pp 
      LEFT JOIN pp.user u 
      WHERE u = :user
      AND ( pp.positionEndDate IS NULL OR pp.positionEndDate > CURRENT_DATE )
      """)
  List<ProjectPosition> findCurrentAndFuturePositionsByUser(User user);

  @Query("""
      SELECT pp 
      FROM ProjectPosition pp 
      LEFT JOIN pp.user u 
      WHERE u = :user
      AND pp.positionStartDate <= CURRENT_DATE 
      AND ( pp.positionEndDate IS NULL OR pp.positionEndDate > CURRENT_DATE )
      """)
  List<ProjectPosition> findCurrentPositionsByUser(User user);
}
