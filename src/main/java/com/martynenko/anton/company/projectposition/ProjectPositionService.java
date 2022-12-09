package com.martynenko.anton.company.projectposition;

import java.util.Collection;

public interface ProjectPositionService {

  ProjectPosition create(ProjectPositionDTO newProjectPosition);

  ProjectPosition update(Long id, ProjectPositionDTO updated);

  ProjectPosition get(Long id);

  Collection<ProjectPosition> listAll();

  void delete(Long id);
}
