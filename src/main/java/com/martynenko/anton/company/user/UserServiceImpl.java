package com.martynenko.anton.company.user;

import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentService;
import com.martynenko.anton.company.projectposition.ProjectPosition;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import com.martynenko.anton.company.utils.DateInterval;
import com.martynenko.anton.company.utils.ScheduleHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@Primary
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final DepartmentService departmentService;

  private final ProjectPositionRepository projectPositionRepository;

  private final ScheduleHelper scheduleHelper;

  public UserServiceImpl(UserRepository userRepository, DepartmentService departmentService,
      ProjectPositionRepository projectPositionRepository, ScheduleHelper scheduleHelper) {
    this.userRepository = userRepository;
    this.departmentService = departmentService;
    this.projectPositionRepository = projectPositionRepository;
    this.scheduleHelper = scheduleHelper;
  }

  @Override
  public User create(final UserDTO newUser) {
    log.debug("Creating new user: {}", newUser);
    Department department = departmentService.get(newUser.departmentId());
    User user = newUser.createInstance(department);
    return userRepository.save(user);
  }

  //to prevent duplication errors file shouldn't be imported partly
  // if exception is thrown during iteration
  // list also should be validated at this point because it wasn't validated in controller
  @Override
  @Transactional
  public void create(@Valid final Collection<UserDTO> newUsers) {
    log.debug("Creating users from list");
    newUsers.forEach(this::create);
  }

  @Override
  public User update(final Long id, final UserDTO updated) {
    log.debug("Updating user with id {}: {}", id,  updated);
    Department department = departmentService.get(updated.departmentId());
    User user = get(id);
    return userRepository.save(user.update(updated, department));
  }

  @Override
  public User get(final Long id) {
    log.debug("Requesting user with id {}", id);
    return userRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No user with id " + id)
    );
  }

  @Override
  public Collection<User> listAll() {
    log.debug("Requesting all users");
    return userRepository.findAll();
  }


  @Override
  public void delete(final Long id) {
    log.debug("Deleting user with id {}", id);
    get(id);
    userRepository.deleteById(id);
  }

  @Override
  public Collection<AvailableUserView> listAvailableUserViews() {
    log.debug("Collecting currently available users");

    Collection<User> users = userRepository.findAllWithoutCurrentProjectPosition();

    return users.stream()
        .map(user -> mapUserToView(user, LocalDate.now(), null))
        .toList();
  }

  @Override
  public Collection<AvailableUserView> listAvailableUserViews(long period) {
    return listAvailableUserViews(new DateInterval(LocalDate.now(), LocalDate.now().plusDays(period)));
  }

  @Override
  public Collection<AvailableUserView> listAvailableUserViews(final DateInterval targetPeriod) {
    log.debug("Collecting users available in period {}", targetPeriod.toString());
    Collection<User> users = userRepository.findAll();

    //if period exists than merge position intervals in different projects and find availability windows

    List<AvailableUserView> availableUserViews = new ArrayList<>();

    for (User user: users) {

      Collection<ProjectPosition> currentAndFuturePositions = projectPositionRepository.findCurrentAndFuturePositionsByUser(user);

      List<DateInterval> availabilityWindows = scheduleHelper.getAvailabilityWindows(currentAndFuturePositions, targetPeriod);

      if (!availabilityWindows.isEmpty()) {
        DateInterval closestWindow = availabilityWindows.get(0);
        LocalDate availableFrom = closestWindow.getStart();
        LocalDate availableTo = closestWindow.getEnd();

        if (availableTo.isEqual(targetPeriod.getEnd())) {
          availableTo = null;
        }
        availableUserViews.add(mapUserToView(user, availableFrom, availableTo));
      }
    }
    return availableUserViews;
  }

  private AvailableUserView mapUserToView(final User user, final LocalDate availableFrom, final LocalDate availableTo) {
    return new AvailableUserView(user.toDTO(), availableFrom, availableTo);
  }
}
