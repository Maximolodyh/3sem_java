package ru.urfu.lab8.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudentForm {
  @NotBlank
  @Size(max = 15)
  private String name = "";

  @NotBlank
  @Size(max = 25)
  private String surname = "";

  @NotBlank
  @Size(max = 20)
  private String faculty = "";

  @Min(14)
  @Max(120)
  private int age = 18;

  private Long groupId;

  public String getName() {
    return name;
  }

  public void setName(String v) {
    name = v;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String v) {
    surname = v;
  }

  public String getFaculty() {
    return faculty;
  }

  public void setFaculty(String v) {
    faculty = v;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int v) {
    age = v;
  }

  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long v) {
    groupId = v;
  }

  public StudentRequest toRequest() {
    return new StudentRequest(name, surname, faculty, age, groupId);
  }
}
