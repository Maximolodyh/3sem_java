package ru.urfu.lab8.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookstores")
public class Bookstore {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 150)
  private String name;
  @Column(nullable = false, length = 200)
  private String address;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private UserAccount owner;

  protected Bookstore() {}
  public Bookstore(String name, String address, UserAccount owner) {
    update(name, address); this.owner = owner;
  }
  public void update(String name, String address) {
    this.name = name.trim(); this.address = address.trim();
  }
  public Long getId() { return id; }
  public String getName() { return name; }
  public String getAddress() { return address; }
  public UserAccount getOwner() { return owner; }
}
