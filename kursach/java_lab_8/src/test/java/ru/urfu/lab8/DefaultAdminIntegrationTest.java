package ru.urfu.lab8;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.urfu.lab8.repository.UserAccountRepository;
import ru.urfu.lab8.service.UserAccountService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:default-admin-tests;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.demo-admin.enabled=true"
})
@AutoConfigureMockMvc
class DefaultAdminIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired UserAccountRepository users;
  @Autowired UserAccountService accounts;
  @Autowired PasswordEncoder encoder;

  @Test
  void defaultAdminIsCreatedHasHashedPasswordAndCanEnterAdminPage() throws Exception {
    var admin = users.findByEmailIgnoreCase("admin@mail.ru").orElseThrow();
    assertTrue(encoder.matches("123123123", admin.getPasswordHash()));
    assertNotEquals("123123123", admin.getPasswordHash());
    assertTrue(admin.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName())));

    MockHttpSession session = (MockHttpSession) mvc.perform(post("/login").with(csrf())
        .param("username", "admin@mail.ru").param("password", "123123123"))
        .andExpect(status().is3xxRedirection())
        .andReturn().getRequest().getSession(false);
    assertNotNull(session);
    mvc.perform(get("/users").session(session)).andExpect(status().isOk());
  }

  @Test
  void repeatedInitializationDoesNotResetAccount() {
    var existing = users.findByEmailIgnoreCase("admin@mail.ru").orElseThrow();
    String storedHash = existing.getPasswordHash();
    long id = existing.getId();
    assertFalse(accounts.createDefaultAdminIfAbsent("admin@mail.ru", "other-password"));
    var after = users.findByEmailIgnoreCase("admin@mail.ru").orElseThrow();
    assertEquals(id, after.getId());
    assertEquals(storedHash, after.getPasswordHash());
  }
}
