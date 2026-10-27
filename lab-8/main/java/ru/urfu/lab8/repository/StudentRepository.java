package ru.urfu.lab8.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
  long countByStudyGroupId(Long groupId);
}
