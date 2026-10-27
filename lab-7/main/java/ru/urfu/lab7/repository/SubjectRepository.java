package ru.urfu.lab7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab7.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  boolean existsByCodeIgnoreCase(String code);
}
