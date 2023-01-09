package com.martynenko.anton.company.project;

import java.util.Collection;

public interface ProjectService {

  Project create(ProjectDTO newProject);

  Project update(Long id, ProjectDTO updated);

  Project get(Long id);

  Collection<Project> listAll();

  void delete(Long id);
}
