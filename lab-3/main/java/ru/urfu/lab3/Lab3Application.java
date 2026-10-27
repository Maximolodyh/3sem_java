package ru.urfu.lab3;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
public class Lab3Application {
  public static void main(String[] a) {
    SpringApplication.run(Lab3Application.class, a);
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
