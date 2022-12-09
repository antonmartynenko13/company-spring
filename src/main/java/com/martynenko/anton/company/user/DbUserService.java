package com.martynenko.anton.company.user;

import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentService;
import java.time.LocalDate;
import java.util.Collection;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@Primary
public class DbUserService implements UserService {

  private final UserRepository userRepository;

  private final DepartmentService departmentService;


  @Autowired
  public DbUserService(final UserRepository userRepository, final DepartmentService departmentService) {
    this.userRepository = userRepository;
    this.departmentService = departmentService;
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
  public Collection<User> listAvailable(final long period) {
    log.debug("Requesting available users");
    LocalDate current = LocalDate.now();
    return userRepository.findAvailable(current, current.plusDays(period));
  }
}
