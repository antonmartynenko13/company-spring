package com.martynenko.anton.company.project;

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

  public ProjectController(final ProjectService projectService) {
    this.projectService = projectService;
  }

  @Create
  @PostMapping
  public ResponseEntity<ProjectDTO> create(@RequestBody @Valid ProjectDTO created,
      final HttpServletRequest request) {
    created =  projectService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @Update
  @PutMapping("/{id}")
  public ResponseEntity<ProjectDTO> update(@PathVariable final Long id,
      @RequestBody @Valid final ProjectDTO updated) {
    return ResponseEntity.ok(projectService.update(id, updated).toDTO());
  }

  @GetById
  @GetMapping("/{id}")
  public ResponseEntity<ProjectDTO> getById(@PathVariable final Long id) {
    return ResponseEntity.ok(projectService.get(id).toDTO());
  }

  @GetAll
  @GetMapping
  public ResponseEntity<Collection<ProjectDTO>> getAll() {
    List<ProjectDTO> projectDTOList = projectService.listAll()
        .stream().map(Project::toDTO).toList();
    log.debug("Found {} projects", projectDTOList.size());
    return ResponseEntity.ok(projectDTOList);
  }


  @Delete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    projectService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
