package ru.urfu.lab6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab6.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {}
