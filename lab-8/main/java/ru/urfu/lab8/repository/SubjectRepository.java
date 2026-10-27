package ru.urfu.lab8.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  boolean existsByCodeIgnoreCase(String code);
}
