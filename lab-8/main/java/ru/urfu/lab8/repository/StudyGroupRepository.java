package ru.urfu.lab8.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
  boolean existsByCodeIgnoreCase(String code);
}
