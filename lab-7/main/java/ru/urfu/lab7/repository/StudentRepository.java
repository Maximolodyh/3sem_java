package ru.urfu.lab7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab7.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
  long countByStudyGroupId(Long groupId);
}
