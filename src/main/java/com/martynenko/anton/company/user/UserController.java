package com.martynenko.anton.company.user;

import com.martynenko.anton.company.csv.CsvHelper;
import com.martynenko.anton.company.openapi.CreateWithRelations;
import com.martynenko.anton.company.openapi.Delete;
import com.martynenko.anton.company.openapi.GetAll;
import com.martynenko.anton.company.openapi.GetById;
import com.martynenko.anton.company.openapi.UpdateWithRelations;
import com.martynenko.anton.company.openapi.GetAvailable;
import com.martynenko.anton.company.openapi.ImportWithCsv;
import com.martynenko.anton.company.projectposition.ProjectPosition;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "users")
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  private final CsvHelper<UserDTO> csvHelper;

  @Autowired
  public UserController(final UserService userService, final CsvHelper<UserDTO> csvHelper) {
    this.userService = userService;
    this.csvHelper = csvHelper;
  }

  @CreateWithRelations
  @PostMapping
  public ResponseEntity<UserDTO> create(@RequestBody @Valid UserDTO created,
      HttpServletRequest request) {
    created =  userService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @ImportWithCsv
  @PostMapping("/import")
  public ResponseEntity<?> create(@RequestParam final MultipartFile file) {

    Collection<UserDTO> userDTOS = csvHelper.readAll(file, UserDTO.class);
    userService.create(userDTOS);
    return ResponseEntity.ok().build();
  }

  @UpdateWithRelations
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> update(@PathVariable final Long id,
      @RequestBody @Valid UserDTO updated) {
    return ResponseEntity.ok(userService.update(id, updated).toDTO());
  }

  @GetById
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getById(@PathVariable final Long id) {
    return ResponseEntity.ok(userService.get(id).toDTO());
  }

  @GetAll
  @GetMapping
  public ResponseEntity<Collection<UserDTO>> getAll() {
    List<UserDTO> userDTOList = userService.listAll().stream().map(User::toDTO).toList();
    log.debug("Found {} users", userDTOList.size());
    return ResponseEntity.ok(userDTOList);
  }

  @Delete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetAvailable
  @GetMapping("/available")
  public ResponseEntity<Collection<AvailableUserView>> getAvailable(
      @RequestParam(defaultValue = "0") final long period) {

    Collection<AvailableUserView> availableUserViews = period == 0 ? userService.listAvailableUserViews()
        : userService.listAvailableUserViews(period);

    log.debug("Found {} available users", availableUserViews.size());
    return ResponseEntity.ok(availableUserViews);
  }
}
