package com.martynenko.anton.company.projectposition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.martynenko.anton.company.project.ProjectServiceImpl;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.user.UserServiceImpl;
import com.martynenko.anton.company.user.User;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

class ProjectPositionServiceImplTest {
  private ProjectPositionRepository repository = mock(ProjectPositionRepository.class);

  private UserServiceImpl userService = mock(UserServiceImpl.class);
  private ProjectServiceImpl projectService = mock(ProjectServiceImpl.class);
  private ProjectPositionServiceImpl service
      = new ProjectPositionServiceImpl(repository, userService, projectService);

  private ProjectPositionDTO dto = mock(ProjectPositionDTO.class);
  private ProjectPosition entity = mock(ProjectPosition.class);

  long userId = 1;
  long projectId = 1;
  private User user = mock(User.class);
  private Project project = mock(Project.class);

  long id = 1;
  long missingId = -1;


  @Test
  void shouldReturnEntity() {

    when(dto.userId()).thenReturn(userId);
    when(userService.get(userId)).thenReturn(user);
    when(dto.projectId()).thenReturn(projectId);
    when(projectService.get(projectId)).thenReturn(project);
    when(dto.createInstance(user, project)).thenReturn(entity);
    when(entity.update(dto, user, project)).thenReturn(entity);
    when(repository.save(entity)).thenReturn(entity);
    when(repository.findById(id)).thenReturn(Optional.of(entity));

    assertThat(service.create(dto)).isEqualTo(entity);

    assertThat(service.update(id, dto)).isEqualTo(entity);

    assertThat(service.get(id)).isEqualTo(entity);
  }

  @Test
  void shouldThrowEntityNotFoundException() {

    assertThrows(EntityNotFoundException.class, () -> {
      service.get(missingId);
    });

    assertThrows(EntityNotFoundException.class, () -> {
      service.update(missingId, dto);
    });

    assertThrows(EntityNotFoundException.class, () -> {
      service.delete(missingId);
    });
  }

  @Test
  void shouldReturnNotEmptyCollection() {
    when(repository.findAll()).thenReturn(List.of(entity));

    assertThat(service.listAll()).isNotEmpty();
  }

  @Test
  void shouldNotThrowAnything() {
    when(dto.id()).thenReturn(id);
    when(repository.findById(id)).thenReturn(Optional.of(entity));
    service.delete(id);
    assertTrue(true);
  }
}