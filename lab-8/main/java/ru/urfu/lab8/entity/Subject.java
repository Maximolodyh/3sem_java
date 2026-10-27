package ru.urfu.lab8.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "subjects",
    uniqueConstraints = @UniqueConstraint(name = "uk_subject_code", columnNames = "code"))
public class Subject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 12)
  private String code;

  @Column(nullable = false, length = 80)
  private String name;

  @Column(nullable = false)
  private int hours;

  protected Subject() {}

  public Subject(String code, String name, int hours) {
    this.code = code;
    this.name = name;
    this.hours = hours;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public int getHours() {
    return hours;
  }

  public void update(String code, String name, int hours) {
    this.code = code;
    this.name = name;
    this.hours = hours;
  }
}
