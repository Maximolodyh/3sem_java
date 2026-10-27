package ru.urfu.lab8.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab8.dto.GroupRequest;
import ru.urfu.lab8.dto.GroupResponse;
import ru.urfu.lab8.entity.StudyGroup;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.exception.NotFoundException;
import ru.urfu.lab8.repository.StudentRepository;
import ru.urfu.lab8.repository.StudyGroupRepository;

@Service
@Transactional(readOnly = true)
public class StudyGroupService {
  private final StudyGroupRepository groups;
  private final StudentRepository students;

  public StudyGroupService(StudyGroupRepository groups, StudentRepository students) {
    this.groups = groups;
    this.students = students;
  }

  public List<GroupResponse> findAll() {
    return groups.findAll().stream().map(this::map).toList();
  }

  public GroupResponse find(long id) {
    return map(entity(id));
  }

  @Transactional
  public GroupResponse create(GroupRequest r) {
    String c = r.code().trim().toUpperCase();
    if (groups.existsByCodeIgnoreCase(c)) throw new ConflictException("Код группы уже существует");
    try {
      return map(groups.saveAndFlush(new StudyGroup(c, r.title().trim())));
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("Код группы уже существует");
    }
  }

  @Transactional
  public GroupResponse update(long id, GroupRequest r) {
    StudyGroup g = entity(id);
    String c = r.code().trim().toUpperCase();
    if (!g.getCode().equalsIgnoreCase(c) && groups.existsByCodeIgnoreCase(c))
      throw new ConflictException("Код группы уже существует");
    g.update(c, r.title().trim());
    try {
      groups.flush();
      return map(g);
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("Код группы уже существует");
    }
  }

  @Transactional
  public void delete(long id) {
    StudyGroup g = entity(id);
    if (students.countByStudyGroupId(id) > 0)
      throw new ConflictException("Нельзя удалить непустую учебную группу");
    groups.delete(g);
  }

  private StudyGroup entity(long id) {
    return groups
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Учебная группа не найдена"));
  }

  private GroupResponse map(StudyGroup g) {
    return new GroupResponse(
        g.getId(), g.getCode(), g.getTitle(), students.countByStudyGroupId(g.getId()));
  }
}
