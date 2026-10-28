package ru.urfu.lab8.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 15)
  private String name;

  @Column(nullable = false, length = 25)
  private String surname;

  @Column(nullable = false, length = 20)
  private String faculty;

  @Column(nullable = false)
  private int age;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  private StudyGroup studyGroup;

  protected Student() {}

  public Student(String name, String surname, String faculty, int age) {
    this.name = name;
    this.surname = surname;
    this.faculty = faculty;
    this.age = age;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public String getFaculty() {
    return faculty;
  }

  public int getAge() {
    return age;
  }

  public void update(String name, String surname, String faculty, int age) {
    this.name = name;
    this.surname = surname;
    this.faculty = faculty;
    this.age = age;
  }

  public StudyGroup getStudyGroup() {
    return studyGroup;
  }

  public void setStudyGroup(StudyGroup group) {
    this.studyGroup = group;
  }
}
