package com.martynenko.anton.company.project;

import com.martynenko.anton.company.openapi.CrudCreate;
import com.martynenko.anton.company.openapi.CrudDelete;
import com.martynenko.anton.company.openapi.CrudGetAll;
import com.martynenko.anton.company.openapi.CrudGetOne;
import com.martynenko.anton.company.openapi.CrudUpdate;
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
@Tag(name = "projects")
@Validated
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
  private final ProjectService projectService;

  @Autowired
  public ProjectController(final ProjectService projectService) {
    this.projectService = projectService;
  }

  @CrudCreate
  @PostMapping("")
  public ResponseEntity<ProjectDTO> create(@RequestBody @Valid ProjectDTO created,
      final HttpServletRequest request) {
    created =  projectService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @CrudUpdate
  @PutMapping("/{id}")
  public ResponseEntity<ProjectDTO> update(@PathVariable final Long id,
      @RequestBody @Valid final ProjectDTO updated) {
    return ResponseEntity.ok(projectService.update(id, updated).toDTO());
  }

  @CrudGetOne
  @GetMapping("/{id}")
  public ResponseEntity<ProjectDTO> getOne(@PathVariable final Long id) {
    return ResponseEntity.ok(projectService.get(id).toDTO());
  }

  @CrudGetAll
  @GetMapping("")
  public ResponseEntity<Collection<ProjectDTO>> getAll() {
    List<ProjectDTO> projectDTOList = projectService.listAll()
        .stream().map(Project::toDTO).toList();
    log.debug("Found {} projects", projectDTOList.size());
    return ResponseEntity.ok(projectDTOList);
  }


  @CrudDelete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    projectService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
