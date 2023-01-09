package com.martynenko.anton.company.project;

import java.util.Collection;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectServiceImpl(final ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public Project create(final ProjectDTO newProject) {
    log.debug("Creating new project: {}", newProject);
    return projectRepository.save(newProject.createInstance());
  }

  @Override
  public Project update(final Long id, final ProjectDTO updated) {
    log.debug("Updating project with id {}: {}", id,  updated);
    return projectRepository.save(get(id).update(updated));
  }

  @Override
  public Project get(final Long id) {
    log.debug("Requesting project with id {}", id);
    return projectRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No project with id " + id)
    );
  }

  @Override
  public Collection<Project> listAll() {
    log.debug("Requesting all departments");
    return projectRepository.findAll();
  }

  @Override
  public void delete(final Long id) {
    log.debug("Deleting department with id {}", id);
    get(id);
    projectRepository.deleteById(id);
  }
}
