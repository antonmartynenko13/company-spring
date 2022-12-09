package com.martynenko.anton.company.department;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

class DbDepartmentServiceTest {

  private DepartmentRepository repository = mock(DepartmentRepository.class);

  private DbDepartmentService service = new DbDepartmentService(repository);

  private DepartmentDTO dto = mock(DepartmentDTO.class);
  private Department entity = mock(Department.class);

  long id = 1;
  long missingId = -1;


  @Test
  void shouldReturnEntity() {

    when(dto.createInstance()).thenReturn(entity);
    when(entity.update(dto)).thenReturn(entity);
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