package ru.urfu.lab8;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import ru.urfu.lab8.repository.RoleRepository;
import ru.urfu.lab8.repository.StudentRepository;
import ru.urfu.lab8.repository.StudyGroupRepository;
import ru.urfu.lab8.repository.UserAccountRepository;
import ru.urfu.lab8.service.UserAccountService;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:lab8web;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@AutoConfigureMockMvc
class WebIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired StudentRepository students;
  @Autowired StudyGroupRepository groups;
  @Autowired UserAccountRepository users;
  @Autowired RoleRepository roles;
  @Autowired UserAccountService accounts;
  MockHttpSession session;

  @BeforeEach
  void clean() throws Exception {
    students.deleteAll();
    groups.deleteAll();
    users.deleteAll();
    roles.deleteAll();
    accounts.bootstrap(
        "Admin",
        "Test",
        "admin@example.test",
        "correct-horse-123",
        Set.of("USER", "EDITOR", "ADMIN"));
    session =
        (MockHttpSession)
            mvc.perform(
                    post("/login")
                        .with(csrf())
                        .param("username", "admin@example.test")
                        .param("password", "correct-horse-123"))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getRequest()
                .getSession(false);
  }

  @Test
  void formRequiresCsrfAndKeepsValidationErrors() throws Exception {
    mvc.perform(post("/students").session(session).param("name", "А"))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/students")
                .session(session)
                .with(csrf())
                .param("name", "")
                .param("surname", "")
                .param("faculty", "")
                .param("age", "5"))
        .andExpect(status().isOk())
        .andExpect(view().name("student-form"))
        .andExpect(
            model().attributeHasFieldErrors("studentForm", "name", "surname", "faculty", "age"));
  }

  @Test
  void groupCannotBeDeletedWhileItContainsStudent() throws Exception {
    String g =
        mvc.perform(
                post("/api/groups")
                    .session(session)
                    .with(csrf())
                    .contentType("application/json")
                    .content("{\"code\":\"РИ-101\",\"title\":\"Первая группа\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = Long.parseLong(g.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
    mvc.perform(
            post("/api/students")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"name\":\"Анна\",\"surname\":\"Иванова\",\"faculty\":\"ИРИТ\",\"age\":20,\"groupId\":"
                        + id
                        + "}"))
        .andExpect(status().isCreated());
    mvc.perform(delete("/api/groups/{id}", id).session(session).with(csrf()))
        .andExpect(status().isConflict());
  }

  @Test
  void pagesAndSafeActuatorAreAvailable() throws Exception {
    mvc.perform(get("/students").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Студенты")));
    mvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
    mvc.perform(get("/actuator/env").session(session)).andExpect(status().isNotFound());
  }
}
