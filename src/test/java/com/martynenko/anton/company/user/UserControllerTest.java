package com.martynenko.anton.company.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martynenko.anton.company.department.Department;
import com.martynenko.anton.company.department.DepartmentDTO;
import com.martynenko.anton.company.department.DepartmentRepository;
import com.martynenko.anton.company.project.Project;
import com.martynenko.anton.company.project.ProjectDTO;
import com.martynenko.anton.company.project.ProjectRepository;
import com.martynenko.anton.company.projectposition.ProjectPositionDTO;
import com.martynenko.anton.company.projectposition.ProjectPositionRepository;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //disable security
@Transactional
class UserControllerTest {
  final String contextPath = "/api/users/";

  final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private JwtDecoder jwtDecoder;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserController userController;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DepartmentRepository departmentRepository;

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  ProjectPositionRepository projectPositionRepository;

  @Test
  void onCreateShouldReturnCreatedWithLocationHeaderOn() throws Exception {
    long departmentId = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance()).getId();
    Map<String, String> payloadMap = Map.of(
        "firstName", "First",
        "lastName", "Last",
        "email", "email@domain.com",
        "jobTitle", "employee",
        "departmentId", String.valueOf(departmentId)
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern(contextPath + "*"));
  }

  @Test
  void onCreateWithDuplicationShouldReturnConflict() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    userRepository.save(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
        ).createInstance(department));

    //duplication of unique title
    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        //email is unique
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onCreateWithoutMandatoryFieldShouldReturnBadRequest() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        //no last name
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap))
            )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onCreateWithoutMandatoryRelationShouldReturnNotFound() throws Exception {

    //duplication of unique title
    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        "email", "email@domain.com",
        "jobTitle", "employee2",
        //no such department
        "departmentId", "100"
    );

    this.mockMvc.perform(post(contextPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateShouldReturnOkWithSameEntity() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    long id = userRepository.save(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department)).getId();

    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
  }

  /*
   * Need this workaround to prevent update with duplicate value inside transaction
   * And I still want to use declaration initialization, so @DirtiesContext will crush context
   * */

  @Test
  @Transactional(propagation = Propagation.NEVER)
  void onUpdateWithDuplicationShouldReturnConflict() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    userRepository.save(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department)).getId();

    long id = userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        //another email
        "email2@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department)).getId();

    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        //duplication
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

    userRepository.deleteAll();
    departmentRepository.deleteAll();
  }

  @Test
  void onUpdateWithoutRequiredFieldShouldReturnBadRequest() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    long id = userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department)).getId();

    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        //no lastname
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateWithoutMandatoryRelationShouldReturnNotFound() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    long id = userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department)).getId();

    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        "email", "email@domain.com",
        "jobTitle", "employee2",
        //no such department
        "departmentId", "100"
    );

    this.mockMvc.perform(put(contextPath + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onUpdateWithMissingIdShouldReturnNotFound() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());
    long missingId = 0;
    Map<String, String> payloadMap = Map.of(
        "firstName", "First2",
        "lastName", "Last2",
        "email", "email@domain.com",
        "jobTitle", "employee2",
        "departmentId", String.valueOf(department.getId())
    );

    this.mockMvc.perform(put(contextPath + missingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsString(payloadMap)))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onGetShouldReturnOkWithSingleEntity() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    long id = userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department)).getId();

    this.mockMvc.perform(get(contextPath + id))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
  }

  @Test
  void onGetWithMissingIdShouldReturnNotFound() throws Exception {
    long missingId = 0;

    this.mockMvc.perform(get(contextPath + missingId))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onDeleteShouldReturnNoContent() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    long id = userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department)).getId();

    this.mockMvc.perform(delete(contextPath + id))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  void onDeleteWithMissingIdShouldReturnNotFound() throws Exception {
    long missingId = 0;

    this.mockMvc.perform(delete(contextPath + missingId))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }


  @Test
  void onGetAllShouldReturnOkWithJsonListOfEntities() throws Exception {
    Department department = departmentRepository.save(new DepartmentDTO(null,"Department1").createInstance());

    userRepository.save(new UserDTO(
        null,
        "First2",
        "Last2",
        "email@domain.com",
        "employee2",
        department.getId()
    ).createInstance(department));

    this.mockMvc.perform(get(contextPath))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
  }

  /*
   * Need this workaround because of a slightly unpredictable behavior of transactions in h2
   * */
  @Test
  @Transactional(propagation = Propagation.NEVER)
  void onGetAvailableWithAvailableUserShouldReturnArrayOfUserDetailsWithAvailableFrom()
      throws Exception {
    createDepartmentUserProjectAndPosition(null, null);

    this.mockMvc.perform(get(contextPath + "/available"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_details").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_from").value(LocalDate.now().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_to").doesNotExist());

    //clear repositories
    clearRepos();
  }

  /*
   * Need this workaround because of a slightly unpredictable behavior of transactions in h2
   * */
  @Test
  @Transactional(propagation = Propagation.NEVER)
  void onGetAvailableWithBusyInFutureUserShouldReturnArrayOfUserDetailsWithAvailableFromAndAvailableTo()
      throws Exception {
    LocalDate userIsBusyFrom = LocalDate.now().plusDays(2);
    LocalDate userIsBusyTo = LocalDate.now().plusDays(7);
    createDepartmentUserProjectAndPosition(userIsBusyFrom, userIsBusyTo);

    this.mockMvc.perform(get(contextPath + "/available")
                          .param("period", "9"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_details").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_from").value(LocalDate.now().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_to").value(userIsBusyFrom.toString()));

    //clear repositories
    clearRepos();
  }

  /*
   * Need this workaround because of a slightly unpredictable behavior of transactions in h2
   * */
  @Test
  @Transactional(propagation = Propagation.NEVER)
  void onGetAvailableWithAvailableInFutureUserShouldReturnArrayOfUserDetailsWithAvailableFrom()
      throws Exception {
    LocalDate userIsBusyFrom = LocalDate.now().minusDays(5);
    LocalDate userIsBusyTo = LocalDate.now().plusDays(2);
    createDepartmentUserProjectAndPosition(userIsBusyFrom, userIsBusyTo);

    this.mockMvc.perform(get(contextPath + "/available")
            .param("period", "9"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_details").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_from").value(userIsBusyTo.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].available_to").doesNotExist());

    //clear repositories
    clearRepos();
  }


  @Test
  void onImportShouldReturnOk() throws Exception {
    long departmentId = departmentRepository.saveAndFlush(new DepartmentDTO(
        null,
        "Some department"
    ).createInstance()).getId();

    String header = "firstName,"
        + "lastName,"
        + "email,"
        + "jobTitle,"
        + "departmentId";

    String value = "Firstname1,"
        + "Lastname2,"
        + "email@domain.com,"
        + "Title 1,"
        + departmentId;

    String fileName = "file";

    MockMultipartFile importFile
        = new MockMultipartFile(
        fileName,
        fileName + ".csv",
        MediaType.TEXT_PLAIN_VALUE,
        (header + "\n" + value).getBytes()
    );

    mockMvc.perform(multipart(contextPath + "import").file(importFile))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void onImportWithMissingRelationShouldReturnNotFound() throws Exception {

    String header = "firstName,"
        + "lastName,"
        + "email,"
        + "jobTitle,"
        + "departmentId";

    String value = "Firstname1,"
        + "Lastname2,"
        + "email@domain.com,"
        + "Title 1,"
        //no such department
        + 100;

    String fileName = "file";

    MockMultipartFile importFile
        = new MockMultipartFile(
        fileName,
        fileName + ".csv",
        MediaType.TEXT_PLAIN_VALUE,
        (header + "\n" + value).getBytes()
    );

    mockMvc.perform(multipart(contextPath + "import").file(importFile))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onImportWithDuplicatesShouldReturnConflict() throws Exception {
    Department department
        = departmentRepository.saveAndFlush(new DepartmentDTO(
        null,
        "Some department"
    ).createInstance());

    User user = userRepository.saveAndFlush(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));

    String header = "firstName,"
        + "lastName,"
        + "email,"
        + "jobTitle,"
        + "departmentId";

    String value = "Firstname1,"
        + "Lastname2,"
        //duplication of unique value
        + "email@domain.com,"
        + "Title 1,"
        + department.getId();

    String fileName = "file";

    MockMultipartFile importFile
        = new MockMultipartFile(
        fileName,
        fileName + ".csv",
        MediaType.TEXT_PLAIN_VALUE,
        (header + "\n" + value).getBytes()
    );

    mockMvc.perform(multipart(contextPath + "import").file(importFile))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  @Test
  void onImportWithNotValidShouldReturnBadRequest() throws Exception {
    Department department
        = departmentRepository.saveAndFlush(new DepartmentDTO(
        null,
        "Some department"
    ).createInstance());

    User user = userRepository.saveAndFlush(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));

    String header = "firstName,"
        + "lastName,"
        + "email,"
        + "jobTitle,"
        + "departmentId";

    String value = "Firstname1,"
        + "Lastname2,"
        //Not valid email
        + "emaildomaincom,"
        + "Title 1,"
        + department.getId();

    String fileName = "file";

    MockMultipartFile importFile
        = new MockMultipartFile(
        fileName,
        fileName + ".csv",
        MediaType.TEXT_PLAIN_VALUE,
        (header + "\n" + value).getBytes()
    );

    mockMvc.perform(multipart(contextPath + "import").file(importFile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
  }

  void createDepartmentUserProjectAndPosition(final LocalDate projectStartDate, final LocalDate projectEndDate) {

    Department department
        = departmentRepository.saveAndFlush(new DepartmentDTO(
        null,
        "Some department"
    ).createInstance());

    User user = userRepository.saveAndFlush(new UserDTO(
        null,
        "First",
        "Last",
        "email@domain.com",
        "employee",
        department.getId()
    ).createInstance(department));

    //if startdate is not null so create dummy project
    if (projectStartDate != null) {
      Project project
          = projectRepository.saveAndFlush(new ProjectDTO(
          null,
          "Project1",
          projectStartDate,
          projectEndDate)
          .createInstance());

      projectPositionRepository.saveAndFlush(
          new ProjectPositionDTO(
              null,
              user.getId(),
              project.getId(),
              projectStartDate,
              projectEndDate,
              "Some title",
              "Some occupation"
          ).createInstance(user, project)
      );
    }
  }

  void clearRepos(){
    projectPositionRepository.deleteAll();
    userRepository.deleteAll();
    projectRepository.deleteAll();
    departmentRepository.deleteAll();
  }

}