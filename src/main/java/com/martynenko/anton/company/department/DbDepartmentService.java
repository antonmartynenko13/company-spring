package com.martynenko.anton.company.department;


import java.util.Collection;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class DbDepartmentService implements DepartmentService {

  @Autowired
  private final DepartmentRepository departmentRepository;

  public DbDepartmentService(final DepartmentRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  @Override
  public Department create(final DepartmentDTO newDepartment) {
    log.debug("Creating new department: {}", newDepartment);
    return departmentRepository.save(newDepartment.createInstance());
  }

  @Override
  public Department update(final Long id, final DepartmentDTO updated) {
    log.debug("Updating department with id {}: {}", id,  updated);
    return departmentRepository.save(get(id).update(updated));
  }

  @Override
  public Department get(final Long id) {
    log.debug("Requesting department with id {}", id);
    return departmentRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No department with id " + id)
    );
  }

  @Override
  public Collection<Department> listAll() {
    log.debug("Requesting all departments");
    return departmentRepository.findAll();
  }

  @Override
  public void delete(final Long id) {
    log.debug("Deleting department with id {}", id);
    get(id);
    departmentRepository.deleteById(id);
  }
}
