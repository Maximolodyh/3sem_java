package ru.urfu.lab8.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 200)
  private String title;
  @Column(nullable = false, length = 150)
  private String author;
  @Column(nullable = false)
  private int publicationYear;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private UserAccount owner;

  protected Book() {}
  public Book(String title, String author, int year, UserAccount owner) {
    update(title, author, year); this.owner = owner;
  }
  public void update(String title, String author, int year) {
    this.title = title.trim(); this.author = author.trim(); this.publicationYear = year;
  }
  public Long getId() { return id; }
  public String getTitle() { return title; }
  public String getAuthor() { return author; }
  public int getPublicationYear() { return publicationYear; }
  public UserAccount getOwner() { return owner; }
}
