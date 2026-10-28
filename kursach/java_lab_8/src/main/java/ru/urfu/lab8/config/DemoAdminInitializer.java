package ru.urfu.lab8.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.urfu.lab8.service.UserAccountService;

/**
 * Для учебной версии создаёт демонстрационный аккаунт при первом запуске.
 * Выключается через app.demo-admin.enabled=false / DEMO_ADMIN_ENABLED=false.
 */
@Component
@ConditionalOnProperty(name = "app.demo-admin.enabled", havingValue = "true")
public class DemoAdminInitializer implements ApplicationRunner {
  private static final Logger log = LoggerFactory.getLogger(DemoAdminInitializer.class);

  private final UserAccountService users;
  private final String email;
  private final String password;

  public DemoAdminInitializer(
      UserAccountService users,
      @Value("${app.demo-admin.email}") String email,
      @Value("${app.demo-admin.password}") String password) {
    this.users = users;
    this.email = email;
    this.password = password;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (users.createDefaultAdminIfAbsent(email, password)) {
      log.warn("Создан учебный администратор: {}. Установите другой пароль перед публикацией.", email);
    } else {
      log.info("Учётная запись {} уже существует; пароль и роли не изменены.", email);
    }
  }
}
