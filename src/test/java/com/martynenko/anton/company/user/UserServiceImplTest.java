package com.martynenko.anton.company.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.martynenko.anton.company.csv.CsvHelper;
import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentServiceImpl;
import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import com.martynenko.anton.company.utils.DateInterval;
import com.martynenko.anton.company.utils.ScheduleHelper;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

class UserServiceImplTest {
  private UserRepository userRepository = mock(UserRepository.class);

  private DepartmentServiceImpl departmentService = mock(DepartmentServiceImpl.class);
  private CsvHelper<UserDTO> csvHelper = mock(CsvHelper.class);
  private ProjectPositionRepository projectPositionRepository = mock(ProjectPositionRepository.class);
  private ScheduleHelper scheduleHelper = mock(ScheduleHelper.class);
  private UserServiceImpl service = new UserServiceImpl(userRepository, departmentService, projectPositionRepository, scheduleHelper);

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
    when(userRepository.save(entity)).thenReturn(entity);
    when(userRepository.findById(id)).thenReturn(Optional.of(entity));

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
    when(userRepository.findAll()).thenReturn(List.of(entity));

    assertThat(service.listAll()).isNotEmpty();
  }

  @Test
  void shouldNotThrowAnything() {
    when(dto.id()).thenReturn(id);
    when(userRepository.findById(id)).thenReturn(Optional.of(entity));
    service.delete(id);
    assertTrue(true);
  }

  @Test
  void whenUserWithoutCurrentPositionOnListAvailableShouldReturnOneViewCollection() {
    when(entity.toDTO()).thenReturn(dto);

    when(userRepository.findAllWithoutCurrentProjectPosition()).thenReturn(Collections.singletonList(entity));

    Collection<AvailableUserView> views = service.listAvailableUserViews();
    assertThat(views).hasSize(1);
    List<AvailableUserView> viewList = (List<AvailableUserView>) views;
    assertThat(viewList.get(0)).isEqualTo(new AvailableUserView(dto, LocalDate.now(), null));
  }

  @Test
  void whenNoUsersWithoutCurrentPositionOnListAvailableShouldReturnEmptyCollection() {

    when(userRepository.findAllWithoutCurrentProjectPosition()).thenReturn(Collections.EMPTY_LIST);

    assertThat(service.listAvailableUserViews()).isEmpty();
  }

  @Test
  void whenNoUsersAvailableDuringPeriodShouldReturnEmptyCollection() {
    int period = 30;
    when(entity.toDTO()).thenReturn(dto);
    when(userRepository.findAll()).thenReturn(Collections.singletonList(entity));

    List<ProjectPosition> projectPositions = Collections.singletonList(mock(ProjectPosition.class));

    when(projectPositionRepository.findCurrentAndFuturePositionsByUser(entity)).thenReturn(projectPositions);

    DateInterval targetPeriod = new DateInterval(LocalDate.now(), LocalDate.now().plusDays(period));

    when(scheduleHelper.getAvailabilityWindows(projectPositions, targetPeriod)).thenReturn(Collections.EMPTY_LIST);

    assertThat(service.listAvailableUserViews(targetPeriod)).isEmpty();
  }
  @Test
  void whenOneUsersAvailableDuringPeriodShouldReturnOneViewCollection() {
    int period = 30;
    when(entity.toDTO()).thenReturn(dto);
    when(userRepository.findAll()).thenReturn(Collections.singletonList(entity));

    List<ProjectPosition> projectPositions = Collections.singletonList(mock(ProjectPosition.class));

    when(projectPositionRepository.findCurrentAndFuturePositionsByUser(entity)).thenReturn(projectPositions);

    DateInterval targetPeriod = new DateInterval(LocalDate.now(), LocalDate.now().plusDays(period));

    List<DateInterval> availabilityWindows
        = Collections.singletonList(new DateInterval(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5)));

    when(scheduleHelper.getAvailabilityWindows(projectPositions, targetPeriod)).thenReturn(availabilityWindows);

    List<AvailableUserView> views = List.of(new AvailableUserView(dto, availabilityWindows.get(0).getStart(), availabilityWindows.get(0).getEnd()));
    assertThat(service.listAvailableUserViews(targetPeriod)).isEqualTo(views);
  }
}