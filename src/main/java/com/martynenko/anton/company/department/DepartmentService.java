package com.martynenko.anton.company.department;

import java.util.Collection;

public interface DepartmentService {

  Department create(DepartmentDTO newDepartment);

  Department update(Long id, DepartmentDTO updated);

  Department get(Long id);

  Collection<Department> listAll();

  void delete(Long id);
}
