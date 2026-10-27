package ru.urfu.lab1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Service;

@Service
public class ValueStore {
  private final List<String> list = new ArrayList<>();
  private final Map<Integer, String> map = new LinkedHashMap<>();
  private final AtomicInteger key = new AtomicInteger();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public List<String> addList(String value) {
    lock.writeLock().lock();
    try {
      list.add(value);
      return List.copyOf(list);
    } finally {
      lock.writeLock().unlock();
    }
  }

  public Map<Integer, String> addMap(String value) {
    lock.writeLock().lock();
    try {
      map.put(key.getAndIncrement(), value);
      return Map.copyOf(map);
    } finally {
      lock.writeLock().unlock();
    }
  }

  public List<String> list() {
    lock.readLock().lock();
    try {
      return List.copyOf(list);
    } finally {
      lock.readLock().unlock();
    }
  }

  public Map<Integer, String> map() {
    lock.readLock().lock();
    try {
      return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    } finally {
      lock.readLock().unlock();
    }
  }

  public String sizes() {
    lock.readLock().lock();
    try {
      return "ArrayList: %d, HashMap: %d".formatted(list.size(), map.size());
    } finally {
      lock.readLock().unlock();
    }
  }
}
