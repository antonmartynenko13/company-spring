package com.martynenko.anton.company.user;

import com.martynenko.anton.company.csv.CsvHelper;
import com.martynenko.anton.company.openapi.CrudCreateWithRelations;
import com.martynenko.anton.company.openapi.CrudDelete;
import com.martynenko.anton.company.openapi.CrudGetAll;
import com.martynenko.anton.company.openapi.CrudGetOne;
import com.martynenko.anton.company.openapi.CrudUpdateWithRelations;
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
import org.springframework.http.MediaType;
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

  @CrudCreateWithRelations
  @PostMapping("")
  public ResponseEntity<UserDTO> create(@RequestBody @Valid UserDTO created,
      HttpServletRequest request) {
    created =  userService.create(created).toDTO();
    return ResponseEntity.created(URI.create(request.getRequestURI() + created.id())).build();
  }

  @ImportWithCsv
  @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> create(@RequestParam final MultipartFile file) {

    Collection<UserDTO> userDTOS = csvHelper.readAll(file, UserDTO.class);
    userService.create(userDTOS);
    return ResponseEntity.ok().build();
  }

  @CrudUpdateWithRelations
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> update(@PathVariable final Long id,
      @RequestBody @Valid UserDTO updated) {
    return ResponseEntity.ok(userService.update(id, updated).toDTO());
  }

  @CrudGetOne
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getOne(@PathVariable final Long id) {
    return ResponseEntity.ok(userService.get(id).toDTO());
  }

  @CrudGetAll
  @GetMapping("")
  public ResponseEntity<Collection<UserDTO>> getAll() {
    List<UserDTO> userDTOList = userService.listAll().stream().map(User::toDTO).toList();
    log.debug("Found {} users", userDTOList.size());
    return ResponseEntity.ok(userDTOList);
  }

  @CrudDelete
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable final Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetAvailable
  @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Collection<AvailableUserView>> getAvailable(
      @RequestParam(defaultValue = "0") final long period) {
    List<AvailableUserView> userDTOList
        = userService.listAvailable(period)
        .stream().map(user -> convertUserToView(user, period)).toList();
    log.debug("Found {} available users", userDTOList.size());
    return ResponseEntity.ok(userDTOList);
  }

  /*
  We turn the user's DTO into an extended DTO,
  which will include additional information about its availability.
  */
  public AvailableUserView convertUserToView(final User user, final long period) {

    ProjectPosition projectPosition = user.getProjectPosition();

    LocalDate startPeriodDate = LocalDate.now();
    LocalDate availableFrom = startPeriodDate;
    LocalDate endPeriodDate = startPeriodDate.plusDays(period);
    LocalDate availableTo = null;

    if (projectPosition != null) {
      LocalDate positionStartDate = projectPosition.getPositionStartDate();
      LocalDate positionEndDate = projectPosition.getPositionEndDate();

      if (startPeriodDate.isBefore(positionStartDate)) {
        // if project position start date is during period he will be available from now

        if (endPeriodDate.isAfter(positionStartDate)) {
          // if project position start date is during period
          // he will be available till planned project starts
          availableTo = positionStartDate;
        }
      } else {
        // if project position start date is in the past he will be available after project ends
        availableFrom = positionEndDate;
      }
    }
    return new AvailableUserView(user.toDTO(), availableFrom, availableTo);
  }
}
