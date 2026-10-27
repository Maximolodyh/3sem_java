package ru.urfu.lab4.service2;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Service2Application {
  public static void main(String[] a) {
    SpringApplication.run(Service2Application.class, a);
  }

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  Supplier<UUID> uuidSupplier() {
    return UUID::randomUUID;
  }
}
