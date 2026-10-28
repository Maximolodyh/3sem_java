package ru.urfu.lab8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class GroupForm {
  @NotBlank
  @Pattern(regexp = "[A-ZА-Я0-9-]{2,20}")
  private String code = "";

  @NotBlank
  @Size(max = 80)
  private String title = "";

  public String getCode() {
    return code;
  }

  public void setCode(String v) {
    code = v;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String v) {
    title = v;
  }

  public GroupRequest toRequest() {
    return new GroupRequest(code, title);
  }
}
