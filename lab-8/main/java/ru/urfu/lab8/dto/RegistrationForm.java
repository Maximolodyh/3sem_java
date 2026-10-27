package ru.urfu.lab8.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {
  @NotBlank
  @Size(max = 40)
  private String firstName = "";

  @NotBlank
  @Size(max = 40)
  private String lastName = "";

  @NotBlank
  @Email
  @Size(max = 120)
  private String email = "";

  @NotBlank
  @Size(min = 8)
  @Utf8ByteLength(max = 72)
  private String password = "";

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String v) {
    firstName = v;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String v) {
    lastName = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v == null ? null : v.trim().toLowerCase(java.util.Locale.ROOT);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String v) {
    password = v;
  }
}
