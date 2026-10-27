package ru.urfu.lab8.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.urfu.lab8.service.UserAccountService;

@Component
@Profile("bootstrap-admin")
public class BootstrapAdmin implements ApplicationRunner {
  private final UserAccountService users;
  private final String email, password, roleList;

  public BootstrapAdmin(
      UserAccountService users,
      @Value("${ADMIN_EMAIL}") String email,
      @Value("${ADMIN_PASSWORD}") String password,
      @Value("${ADMIN_ROLES:USER,EDITOR,ADMIN}") String roleList) {
    this.users = users;
    this.email = email;
    this.password = password;
    this.roleList = roleList;
  }

  @Override
  public void run(ApplicationArguments args) {
    int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
    if (password.length() < 12 || passwordBytes > 72)
      throw new IllegalStateException(
          "ADMIN_PASSWORD должен содержать не менее 12 символов и не более 72 байт UTF-8");
    Set<String> roles =
        Arrays.stream(roleList.split(","))
            .map(String::trim)
            .collect(java.util.stream.Collectors.toSet());
    users.bootstrap("System", "Administrator", email, password, roles);
    System.out.println(
        "Учетная запись администратора подготовлена: " + email.toLowerCase(Locale.ROOT));
  }
}
