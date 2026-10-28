package ru.urfu.lab8.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
  List<Book> findAllByOrderByIdDesc();
  List<Book> findByOwnerIdOrderByIdDesc(Long ownerId);
}
