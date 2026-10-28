package ru.urfu.lab8.dto;

import jakarta.validation.constraints.*;

public class StoreForm {
  @NotBlank @Size(max = 150)
  private String name;
  @NotBlank @Size(max = 200)
  private String address;
  public String getName() { return name; }
  public void setName(String value) { name = value; }
  public String getAddress() { return address; }
  public void setAddress(String value) { address = value; }
}
