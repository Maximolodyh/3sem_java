package ru.urfu.lab6.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab6.dto.SubjectRequest;
import ru.urfu.lab6.dto.SubjectResponse;
import ru.urfu.lab6.entity.Subject;
import ru.urfu.lab6.exception.ConflictException;
import ru.urfu.lab6.exception.NotFoundException;
import ru.urfu.lab6.repository.SubjectRepository;

@Service
@Transactional(readOnly = true)
public class SubjectService {
  private final SubjectRepository repository;

  public SubjectService(SubjectRepository repository) {
    this.repository = repository;
  }

  public List<SubjectResponse> findAll() {
    return repository.findAll().stream().map(this::map).toList();
  }

  public SubjectResponse find(long id) {
    return map(entity(id));
  }

  @Transactional
  public SubjectResponse create(SubjectRequest r) {
    String code = r.code().trim().toUpperCase();
    if (repository.existsByCodeIgnoreCase(code))
      throw new ConflictException("Код дисциплины уже существует");
    try {
      return map(repository.saveAndFlush(new Subject(code, r.name().trim(), r.hours())));
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("Код дисциплины уже существует");
    }
  }

  @Transactional
  public SubjectResponse update(long id, SubjectRequest r) {
    Subject s = entity(id);
    String code = r.code().trim().toUpperCase();
    if (!s.getCode().equalsIgnoreCase(code) && repository.existsByCodeIgnoreCase(code))
      throw new ConflictException("Код дисциплины уже существует");
    s.update(code, r.name().trim(), r.hours());
    try {
      repository.flush();
      return map(s);
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("Код дисциплины уже существует");
    }
  }

  @Transactional
  public void delete(long id) {
    if (!repository.existsById(id)) throw new NotFoundException("Дисциплина не найдена");
    repository.deleteById(id);
  }

  private Subject entity(long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Дисциплина не найдена"));
  }

  private SubjectResponse map(Subject s) {
    return new SubjectResponse(s.getId(), s.getCode(), s.getName(), s.getHours());
  }
}
