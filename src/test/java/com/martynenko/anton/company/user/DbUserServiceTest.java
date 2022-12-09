package com.martynenko.anton.company.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.martynenko.anton.company.csv.CsvHelper;
import com.martynenko.anton.company.department.DbDepartmentService;
import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentDTO;
import com.martynenko.anton.company.department.DepartmentRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DbUserServiceTest {
  private UserRepository repository = mock(UserRepository.class);

  private DbDepartmentService departmentService = mock(DbDepartmentService.class);
  private CsvHelper<UserDTO> csvHelper = mock(CsvHelper.class);
  private DbUserService service = new DbUserService(repository, departmentService);

  long departmentId = 1;
  private Department departmentEntity = mock(Department.class);
  private UserDTO dto = mock(UserDTO.class);
  private User entity = mock(User.class);

  long id = 1;
  long missingId = -1;


  @Test
  void shouldReturnEntity() {

    when(dto.departmentId()).thenReturn(departmentId);
    when(departmentService.get(departmentId)).thenReturn(departmentEntity);
    when(dto.createInstance(departmentEntity)).thenReturn(entity);
    when(entity.update(dto, departmentEntity)).thenReturn(entity);
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

  @Test
  void onListAvailableShouldReturnNotEmptyCollection() {
    int period = 0;
    LocalDate periodStartDate = LocalDate.now();
    LocalDate periodEndDate = LocalDate.now().plusDays(period);
    when(repository.findAvailable(periodStartDate, periodEndDate))
        .thenReturn(List.of(entity));

    assertThat(service.listAvailable(period)).isNotEmpty();
  }

  @Test
  void onListAvailableShouldReturnEmptyCollection() {
    int period = 0;
    LocalDate periodStartDate = LocalDate.now();
    LocalDate periodEndDate = LocalDate.now().plusDays(period);
    when(repository.findAvailable(periodStartDate, periodEndDate))
        .thenReturn(Collections.EMPTY_LIST);

    assertThat(service.listAvailable(period)).isEmpty();
  }
}