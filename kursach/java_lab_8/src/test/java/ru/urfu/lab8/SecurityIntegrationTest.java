package ru.urfu.lab8;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
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
import ru.urfu.lab8.repository.UserAccountRepository;
import ru.urfu.lab8.service.UserAccountService;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:lab8security;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@AutoConfigureMockMvc
class SecurityIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired UserAccountService accounts;
  @Autowired UserAccountRepository users;
  @Autowired RoleRepository roles;
  @Autowired StudentRepository students;

  @BeforeEach
  void clean() {
    students.deleteAll();
    users.deleteAll();
    roles.deleteAll();
  }

  @Test
  void anonymousApiGets401ButHtmlRedirectsToLogin() throws Exception {
    mvc.perform(get("/api/students")).andExpect(status().isUnauthorized());
    mvc.perform(get("/students"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void registrationAlwaysAssignsOnlyReadOnlyRole() throws Exception {
    mvc.perform(
            post("/register")
                .with(csrf())
                .param("firstName", "Иван")
                .param("lastName", "Иванов")
                .param("email", "IVAN@example.test ")
                .param("password", "long-password-123")
                .param("role", "ADMIN"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?registered"));
    var u = users.findByEmailIgnoreCase("ivan@example.test").orElseThrow();
    org.junit.jupiter.api.Assertions.assertEquals(
        Set.of("ROLE_READ_ONLY"),
        u.getRoles().stream().map(x -> x.getName()).collect(java.util.stream.Collectors.toSet()));
  }

  @Test
  void registrationRejectsPasswordLongerThan72Utf8Bytes() throws Exception {
    mvc.perform(
            post("/register")
                .with(csrf())
                .param("firstName", "Иван")
                .param("lastName", "Иванов")
                .param("email", "long@example.test")
                .param("password", "😀".repeat(19)))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeHasFieldErrors("registrationForm", "password"));
    org.junit.jupiter.api.Assertions.assertFalse(
        users.existsByEmailIgnoreCase("long@example.test"));
  }

  @Test
  void userEditorAndAdminHaveDifferentWriteRights() throws Exception {
    accounts.bootstrap("U", "Test", "user@example.test", "long-password-123", Set.of("USER"));
    accounts.bootstrap(
        "E", "Test", "editor@example.test", "long-password-123", Set.of("USER", "EDITOR"));
    accounts.bootstrap(
        "A", "Test", "admin@example.test", "long-password-123", Set.of("USER", "EDITOR", "ADMIN"));
    MockHttpSession user = login("user@example.test"),
        editor = login("editor@example.test"),
        admin = login("admin@example.test");
    String body = "{\"name\":\"Анна\",\"surname\":\"Иванова\",\"faculty\":\"ИРИТ\",\"age\":20}";
    mvc.perform(get("/api/students").session(user)).andExpect(status().isOk());
    mvc.perform(get("/students/new").session(user)).andExpect(status().isForbidden());
    mvc.perform(get("/groups/new").session(user)).andExpect(status().isForbidden());
    mvc.perform(
            post("/api/students")
                .session(user)
                .with(csrf())
                .contentType("application/json")
                .content(body))
        .andExpect(status().isForbidden());
    String location =
        mvc.perform(
                post("/api/students")
                    .session(editor)
                    .with(csrf())
                    .contentType("application/json")
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");
    mvc.perform(delete(location).session(editor).with(csrf())).andExpect(status().isForbidden());
    mvc.perform(delete(location).session(admin).with(csrf())).andExpect(status().isNoContent());
  }

  @Test
  void csrfAndLiveAccountRefreshAreEnforced() throws Exception {
    accounts.bootstrap(
        "E", "Test", "editor@example.test", "long-password-123", Set.of("USER", "EDITOR"));
    MockHttpSession editor = login("editor@example.test");
    String body = "{\"name\":\"Анна\",\"surname\":\"Иванова\",\"faculty\":\"ИРИТ\",\"age\":20}";
    mvc.perform(post("/api/students").session(editor).contentType("application/json").content(body))
        .andExpect(status().isForbidden());
    accounts.bootstrap("E", "Test", "editor@example.test", "long-password-123", Set.of("USER"));
    mvc.perform(
            post("/api/students")
                .session(editor)
                .with(csrf())
                .contentType("application/json")
                .content(body))
        .andExpect(status().isForbidden());
    var u = users.findByEmailIgnoreCase("editor@example.test").orElseThrow();
    u.setActive(false);
    users.saveAndFlush(u);
    mvc.perform(get("/api/students").session(editor)).andExpect(status().isUnauthorized());
  }

  private MockHttpSession login(String email) throws Exception {
    return (MockHttpSession)
        mvc.perform(
                post("/login")
                    .with(csrf())
                    .param("username", email)
                    .param("password", "long-password-123"))
            .andExpect(status().is3xxRedirection())
            .andReturn()
            .getRequest()
            .getSession(false);
  }
}
