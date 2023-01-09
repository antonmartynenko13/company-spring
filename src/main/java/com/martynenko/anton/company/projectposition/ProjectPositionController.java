package com.martynenko.anton.company.projectposition;

import com.martynenko.anton.company.openapi.CreateWithRelations;
import com.martynenko.anton.company.openapi.Delete;
import com.martynenko.anton.company.openapi.GetAll;
import com.martynenko.anton.company.openapi.GetById;
import com.martynenko.anton.company.openapi.UpdateWithRelations;
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
@Tag(name = "project-positions")
@Validated
@RestController
@RequestMapping("/api/project-positions")
public class ProjectPositionController {
  private final ProjectPositionService projectPositionService;
  
  public ProjectPositionController(final ProjectPositionService projectPositionService) {
    this.projectPositionService = projectPositionService;
  }

  @CreateWithRelations
  @PostMapping
  public ResponseEntity<ProjectPositionDTO> create(
      @RequestBody @Valid ProjectPositionDTO created,
      final HttpServletRequest request) {
    created =  projectPositionService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @UpdateWithRelations
  @PutMapping("/{id}")
  public ResponseEntity<ProjectPositionDTO> update(@PathVariable final Long id,
      @RequestBody @Valid final ProjectPositionDTO updated) {
    return ResponseEntity.ok(projectPositionService.update(id, updated).toDTO());
  }

  @GetById
  @GetMapping("/{id}")
  public ResponseEntity<ProjectPositionDTO> getById(@PathVariable final Long id) {
    return ResponseEntity.ok(projectPositionService.get(id).toDTO());
  }

  @GetAll
  @GetMapping
  public ResponseEntity<Collection<ProjectPositionDTO>> getAll() {
    List<ProjectPositionDTO> projectPositionDTOList
        = projectPositionService.listAll().stream().map(ProjectPosition::toDTO).toList();
    log.debug("Found {} projectpositions", projectPositionDTOList.size());
    return ResponseEntity.ok(projectPositionDTOList);
  }

  @Delete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    projectPositionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
