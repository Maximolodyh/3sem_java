package ru.urfu.lab8.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.Bookstore;

public interface BookstoreRepository extends JpaRepository<Bookstore, Long> {
  List<Bookstore> findAllByOrderByIdDesc();
  List<Bookstore> findByOwnerIdOrderByIdDesc(Long ownerId);
}
