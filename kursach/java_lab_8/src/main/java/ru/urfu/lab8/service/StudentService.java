package ru.urfu.lab8.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab8.dto.StudentRequest;
import ru.urfu.lab8.dto.StudentResponse;
import ru.urfu.lab8.entity.Student;
import ru.urfu.lab8.exception.NotFoundException;
import ru.urfu.lab8.repository.StudentRepository;
import ru.urfu.lab8.repository.StudyGroupRepository;

@Service
@Transactional(readOnly = true)
public class StudentService {
  private final StudentRepository repository;
  private final StudyGroupRepository groups;

  public StudentService(StudentRepository repository, StudyGroupRepository groups) {
    this.repository = repository;
    this.groups = groups;
  }

  public List<StudentResponse> findAll() {
    return repository.findAll().stream().map(this::map).toList();
  }

  public StudentResponse find(long id) {
    return map(entity(id));
  }

  @Transactional
  public StudentResponse create(StudentRequest r) {
    Student s = new Student(r.name().trim(), r.surname().trim(), r.faculty().trim(), r.age());
    s.setStudyGroup(group(r.groupId()));
    return map(repository.save(s));
  }

  @Transactional
  public StudentResponse update(long id, StudentRequest r) {
    Student s = entity(id);
    s.update(r.name().trim(), r.surname().trim(), r.faculty().trim(), r.age());
    s.setStudyGroup(group(r.groupId()));
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

  private ru.urfu.lab8.entity.StudyGroup group(Long id) {
    return id == null
        ? null
        : groups.findById(id).orElseThrow(() -> new NotFoundException("Учебная группа не найдена"));
  }

  private StudentResponse map(Student s) {
    var g = s.getStudyGroup();
    return new StudentResponse(
        s.getId(),
        s.getName(),
        s.getSurname(),
        s.getFaculty(),
        s.getAge(),
        g == null ? null : g.getId(),
        g == null ? null : g.getCode());
  }
}
