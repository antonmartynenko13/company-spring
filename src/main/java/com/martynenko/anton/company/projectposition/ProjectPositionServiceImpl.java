package com.martynenko.anton.company.projectposition;

import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.project.ProjectService;
import com.martynenko.anton.company.user.User;
import com.martynenko.anton.company.user.UserService;
import java.util.Collection;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjectPositionServiceImpl implements ProjectPositionService {
  private ProjectPositionRepository projectPositionRepository;
  private UserService userService;
  private ProjectService projectService;

  public ProjectPositionServiceImpl(final ProjectPositionRepository projectPositionRepository,
      final UserService userService, final ProjectService projectService) {
    this.projectPositionRepository = projectPositionRepository;
    this.userService = userService;
    this.projectService = projectService;
  }

  @Override
  public ProjectPosition create(final ProjectPositionDTO newProjectPosition) {
    log.debug("Creating new projectPosition: {}", newProjectPosition);
    User user = userService.get(newProjectPosition.userId());

    Project project = projectService.get(newProjectPosition.projectId());

    ProjectPosition projectPosition = newProjectPosition.createInstance(user, project);
    return projectPositionRepository.save(projectPosition);
  }

  @Override
  public ProjectPosition update(final Long id, final ProjectPositionDTO updated) {
    log.debug("Updating projectPosition with id {}: {}", id,  updated);
    User user = userService.get(updated.userId());
    Project project = projectService.get(updated.projectId());

    ProjectPosition projectPosition = get(id);
    return projectPositionRepository.save(projectPosition.update(updated, user, project));
  }

  @Override
  public ProjectPosition get(final Long id) {
    log.debug("Requesting projectPosition with id {}", id);
    return projectPositionRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No project position with id " + id)
    );
  }

  @Override
  public Collection<ProjectPosition> listAll() {
    log.debug("Requesting all projectPositions");
    return projectPositionRepository.findAll();
  }

  @Override
  public void delete(final Long id) {
    log.debug("Deleting projectPosition with id {}", id);
    get(id);
    projectPositionRepository.deleteById(id);
  }
}
