package ru.urfu.lab6.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab6.dto.StudentRequest;
import ru.urfu.lab6.dto.StudentResponse;
import ru.urfu.lab6.entity.Student;
import ru.urfu.lab6.exception.NotFoundException;
import ru.urfu.lab6.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class StudentService {
  private final StudentRepository repository;

  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<StudentResponse> findAll() {
    return repository.findAll().stream().map(this::map).toList();
  }

  public StudentResponse find(long id) {
    return map(entity(id));
  }

  @Transactional
  public StudentResponse create(StudentRequest r) {
    return map(
        repository.save(
            new Student(r.name().trim(), r.surname().trim(), r.faculty().trim(), r.age())));
  }

  @Transactional
  public StudentResponse update(long id, StudentRequest r) {
    Student s = entity(id);
    s.update(r.name().trim(), r.surname().trim(), r.faculty().trim(), r.age());
    return map(s);
  }

  @Transactional
  public void delete(long id) {
    if (!repository.existsById(id)) throw new NotFoundException("Студент не найден");
    repository.deleteById(id);
  }

  private Student entity(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("Студент не найден"));
  }

  private StudentResponse map(Student s) {
    return new StudentResponse(s.getId(), s.getName(), s.getSurname(), s.getFaculty(), s.getAge());
  }
}
