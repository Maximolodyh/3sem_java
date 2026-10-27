package ru.urfu.lab7.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "study_groups",
    uniqueConstraints = @UniqueConstraint(name = "uk_group_code", columnNames = "code"))
public class StudyGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String code;

  @Column(nullable = false, length = 80)
  private String title;

  protected StudyGroup() {}

  public StudyGroup(String code, String title) {
    this.code = code;
    this.title = title;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public void update(String code, String title) {
    this.code = code;
    this.title = title;
  }
}
