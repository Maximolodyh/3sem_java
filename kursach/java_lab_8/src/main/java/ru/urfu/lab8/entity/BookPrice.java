package ru.urfu.lab8.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "book_prices", uniqueConstraints = @UniqueConstraint(
    name = "uk_book_store", columnNames = {"book_id", "store_id"}))
public class BookPrice {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "store_id", nullable = false)
  private Bookstore store;
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  protected BookPrice() {}
  public BookPrice(Book book, Bookstore store, BigDecimal price) {
    this.book = book; this.store = store; this.price = price;
  }
  public void update(Book book, Bookstore store, BigDecimal price) {
    this.book = book; this.store = store; this.price = price;
  }
  public Long getId() { return id; }
  public Book getBook() { return book; }
  public Bookstore getStore() { return store; }
  public BigDecimal getPrice() { return price; }
}
