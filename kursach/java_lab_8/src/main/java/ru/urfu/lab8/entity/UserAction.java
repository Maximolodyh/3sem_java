package ru.urfu.lab8.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_actions")
public class UserAction {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "date_actions", nullable = false)
  private LocalDateTime dateActions;
  @Column(nullable = false, length = 120)
  private String username;
  @Column(nullable = false, length = 500)
  private String description;

  protected UserAction() {}
  public UserAction(String username, String description) {
    this.username = username; this.description = description;
    this.dateActions = LocalDateTime.now();
  }
  public Long getId() { return id; }
  public LocalDateTime getDateActions() { return dateActions; }
  public String getUsername() { return username; }
  public String getDescription() { return description; }
}
