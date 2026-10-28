package ru.urfu.lab8;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import ru.urfu.lab8.repository.SubjectRepository;
import ru.urfu.lab8.repository.UserAccountRepository;
import ru.urfu.lab8.service.UserAccountService;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:lab8;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@AutoConfigureMockMvc
class ApiIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired StudentRepository students;
  @Autowired SubjectRepository subjects;
  @Autowired UserAccountRepository users;
  @Autowired RoleRepository roles;
  @Autowired UserAccountService accounts;
  MockHttpSession session;

  @BeforeEach
  void clean() throws Exception {
    students.deleteAll();
    subjects.deleteAll();
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
  void studentCrudAndNotFound() throws Exception {
    String body = "{\"name\":\"Анна\",\"surname\":\"Иванова\",\"faculty\":\"ИРИТ\",\"age\":20}";
    String location =
        mvc.perform(
                post("/api/students")
                    .session(session)
                    .with(csrf())
                    .contentType("application/json")
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Анна"))
            .andReturn()
            .getResponse()
            .getHeader("Location");
    mvc.perform(get(location).session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.age").value(20));
    mvc.perform(
            put(location)
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content(body.replace("20", "21")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.age").value(21));
    mvc.perform(delete(location).session(session).with(csrf())).andExpect(status().isNoContent());
    mvc.perform(get(location).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void reportsAllValidationErrorsAndMalformedJson() throws Exception {
    mvc.perform(
            post("/api/students")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"name\":\"\",\"surname\":\"\",\"faculty\":\"\",\"age\":5}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations.name").exists())
        .andExpect(jsonPath("$.violations.surname").exists())
        .andExpect(jsonPath("$.violations.faculty").exists())
        .andExpect(jsonPath("$.violations.age").exists());
    mvc.perform(
            post("/api/students")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content("{"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Некорректный JSON"));
  }

  @Test
  void subjectCrudAndUniqueCodeConflict() throws Exception {
    String body = "{\"code\":\"CS-101\",\"name\":\"Программирование\",\"hours\":72}";
    mvc.perform(
            post("/api/subjects")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("CS-101"));
    mvc.perform(
            post("/api/subjects")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content(body))
        .andExpect(status().isConflict());
    mvc.perform(get("/api/subjects").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].hours").value(72));
  }
}
