package ru.urfu.lab8;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import ru.urfu.lab8.repository.*;
import ru.urfu.lab8.service.UserAccountService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:catalogtests;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.file.name=logs/catalog-tests.log"
})
@AutoConfigureMockMvc
class CatalogIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired UserAccountService accounts;
  @Autowired UserAccountRepository users;
  @Autowired RoleRepository roles;
  @Autowired BookRepository books;
  @Autowired BookstoreRepository stores;
  @Autowired BookPriceRepository prices;
  @Autowired UserActionRepository logs;

  @BeforeEach
  void cleanup() {
    prices.deleteAll(); books.deleteAll(); stores.deleteAll();
    logs.deleteAll(); users.deleteAll(); roles.deleteAll();
    accounts.bootstrap("User1", "Test", "one@example.test", "password-12345", Set.of("USER"));
    accounts.bootstrap("User2", "Test", "two@example.test", "password-12345", Set.of("USER"));
    accounts.bootstrap("Admin", "Test", "admin@example.test", "password-12345", Set.of("ADMIN", "USER"));
  }

  MockHttpSession login(String email) throws Exception {
    return (MockHttpSession) mvc.perform(post("/login").with(csrf())
        .param("username", email).param("password", "password-12345"))
        .andExpect(status().is3xxRedirection()).andReturn().getRequest().getSession(false);
  }

  @Test
  void registrationGetsReadOnlyAndCannotWrite() throws Exception {
    mvc.perform(post("/register").with(csrf()).param("firstName", "Иван")
        .param("lastName", "Тест").param("email", "reader@example.test")
        .param("password", "password-12345").param("role", "ADMIN"))
        .andExpect(status().is3xxRedirection());
    var reader = users.findByEmailIgnoreCase("reader@example.test").orElseThrow();
    org.junit.jupiter.api.Assertions.assertEquals(Set.of("ROLE_READ_ONLY"),
        reader.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()));
    var session = login("reader@example.test");
    mvc.perform(get("/books").session(session)).andExpect(status().isOk());
    mvc.perform(get("/books/new").session(session)).andExpect(status().isForbidden());
    mvc.perform(post("/books").session(session).with(csrf())
        .param("title", "Test").param("author", "Author").param("publicationYear", "2020"))
        .andExpect(status().isForbidden());
    mvc.perform(get("/users").session(session)).andExpect(status().isForbidden());
  }

  @Test
  void userSeesOnlyOwnDataAndCannotChangeOtherUserData() throws Exception {
    var one = login("one@example.test");
    var two = login("two@example.test");
    mvc.perform(post("/books").session(one).with(csrf())
        .param("title", "Only one").param("author", "Author A").param("publicationYear", "2019"))
        .andExpect(status().is3xxRedirection());
    long id = books.findAll().get(0).getId();
    mvc.perform(get("/books").session(two)).andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.not(
            org.hamcrest.Matchers.containsString("Only one"))));
    mvc.perform(get("/books/{id}/edit", id).session(two)).andExpect(status().isForbidden());
    mvc.perform(post("/books/{id}/delete", id).session(two).with(csrf()))
        .andExpect(status().isForbidden());
    org.junit.jupiter.api.Assertions.assertEquals(1, books.count());
    mvc.perform(get("/books").session(login("admin@example.test")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Only one")));
  }

  @Test
  void booksStoresPricesCalculatorAuditAndRoles() throws Exception {
    var one = login("one@example.test");
    mvc.perform(post("/books").session(one).with(csrf())
        .param("title", "Java Spring").param("author", "Автор")
        .param("publicationYear", "2022")).andExpect(status().is3xxRedirection());
    mvc.perform(post("/bookstores").session(one).with(csrf())
        .param("name", "Магазин А").param("address", "Екатеринбург"))
        .andExpect(status().is3xxRedirection());
    long b = books.findAll().get(0).getId();
    long s = stores.findAll().get(0).getId();
    mvc.perform(post("/prices").session(one).with(csrf())
        .param("bookId", Long.toString(b)).param("storeId", Long.toString(s))
        .param("price", "199.50")).andExpect(status().is3xxRedirection());
    long pr = prices.findAll().get(0).getId();
    mvc.perform(get("/prices").session(one)).andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("199.50")));
    mvc.perform(post("/calculator").session(one).with(csrf())
        .param("priceId", Long.toString(pr)).param("quantity", "3"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("598.50")));
    mvc.perform(post("/prices").session(one).with(csrf())
        .param("bookId", Long.toString(b)).param("storeId", Long.toString(s))
        .param("price", "100.00"))
        .andExpect(view().name("price-form"));
    org.junit.jupiter.api.Assertions.assertTrue(logs.count() >= 4);
    var admin = login("admin@example.test");
    mvc.perform(get("/logs").session(one)).andExpect(status().isForbidden());
    mvc.perform(get("/logs").session(admin)).andExpect(status().isOk());
    mvc.perform(post("/users/{id}/roles", users.findByEmailIgnoreCase("two@example.test").orElseThrow().getId())
        .session(admin).with(csrf()).param("role", "ADMIN"))
        .andExpect(status().is3xxRedirection());
    mvc.perform(get("/users").session(login("two@example.test"))).andExpect(status().isOk());
    mvc.perform(get("/actuator/health")).andExpect(status().isOk());
  }
}
