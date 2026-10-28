package ru.urfu.lab8.dto;

import jakarta.validation.constraints.*;

public class BookForm {
  @NotBlank @Size(max = 200)
  private String title;
  @NotBlank @Size(max = 150)
  private String author;
  @NotNull @Min(1000) @Max(2100)
  private Integer publicationYear;

  public String getTitle() { return title; }
  public void setTitle(String value) { title = value; }
  public String getAuthor() { return author; }
  public void setAuthor(String value) { author = value; }
  public Integer getPublicationYear() { return publicationYear; }
  public void setPublicationYear(Integer value) { publicationYear = value; }
}
