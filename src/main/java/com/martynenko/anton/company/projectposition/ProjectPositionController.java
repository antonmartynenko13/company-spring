package com.martynenko.anton.company.projectposition;

import com.martynenko.anton.company.openapi.CrudCreateWithRelations;
import com.martynenko.anton.company.openapi.CrudDelete;
import com.martynenko.anton.company.openapi.CrudGetAll;
import com.martynenko.anton.company.openapi.CrudGetOne;
import com.martynenko.anton.company.openapi.CrudUpdateWithRelations;
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
@Tag(name = "project-positions")
@Validated
@RestController
@RequestMapping("/api/project-positions")
public class ProjectPositionController {
  private final ProjectPositionService projectPositionService;
  
  @Autowired
  public ProjectPositionController(final ProjectPositionService projectPositionService) {
    this.projectPositionService = projectPositionService;
  }

  @CrudCreateWithRelations
  @PostMapping("")
  public ResponseEntity<ProjectPositionDTO> create(
      @RequestBody @Valid ProjectPositionDTO created,
      final HttpServletRequest request) {
    created =  projectPositionService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @CrudUpdateWithRelations
  @PutMapping("/{id}")
  public ResponseEntity<ProjectPositionDTO> update(@PathVariable final Long id,
      @RequestBody @Valid final ProjectPositionDTO updated) {
    return ResponseEntity.ok(projectPositionService.update(id, updated).toDTO());
  }

  @CrudGetOne
  @GetMapping("/{id}")
  public ResponseEntity<ProjectPositionDTO> getOne(@PathVariable final Long id) {
    return ResponseEntity.ok(projectPositionService.get(id).toDTO());
  }

  @CrudGetAll
  @GetMapping("")
  public ResponseEntity<Collection<ProjectPositionDTO>> getAll() {
    List<ProjectPositionDTO> projectPositionDTOList
        = projectPositionService.listAll().stream().map(ProjectPosition::toDTO).toList();
    log.debug("Found {} projectpositions", projectPositionDTOList.size());
    return ResponseEntity.ok(projectPositionDTOList);
  }

  @CrudDelete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    projectPositionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
