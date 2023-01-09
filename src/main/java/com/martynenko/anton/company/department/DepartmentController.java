package com.martynenko.anton.company.department;

import com.martynenko.anton.company.openapi.Create;
import com.martynenko.anton.company.openapi.Delete;
import com.martynenko.anton.company.openapi.GetAll;
import com.martynenko.anton.company.openapi.GetById;
import com.martynenko.anton.company.openapi.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "departments")
@Validated
@RestController
@RequestMapping("/api/departments/")
public class DepartmentController {

  private DepartmentService departmentService;

  public DepartmentController(final DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @Create
  @PostMapping
  public ResponseEntity create(@RequestBody @Valid DepartmentDTO created,
      final HttpServletRequest request) {
    created =  departmentService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @Update
  @PutMapping("/{id}")
  public ResponseEntity<DepartmentDTO> update(@PathVariable final Long id,
      @RequestBody @Valid final DepartmentDTO updated) {
    return ResponseEntity.ok(departmentService.update(id, updated).toDTO());
  }

  @GetById
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDTO> getById(@PathVariable final Long id) {
    return ResponseEntity.ok(departmentService.get(id).toDTO());
  }

  @GetAll
  @GetMapping
  public ResponseEntity<Collection<DepartmentDTO>> getAll() {
    List<DepartmentDTO> departmentDTOList = departmentService.listAll()
        .stream().map(Department::toDTO).toList();
    log.debug("Found {} departments", departmentDTOList.size());
    return ResponseEntity.ok(departmentDTOList);
  }

  @Delete
  @DeleteMapping("/{id}")
  public ResponseEntity delete(@PathVariable final Long id) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
