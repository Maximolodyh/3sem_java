package ru.urfu.lab8.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import ru.urfu.lab8.entity.BookPrice;

public interface BookPriceRepository extends JpaRepository<BookPrice, Long> {
  @EntityGraph(attributePaths = {"book", "store"})
  List<BookPrice> findAllByOrderByIdDesc();
  @EntityGraph(attributePaths = {"book", "store"})
  List<BookPrice> findByBookOwnerIdAndStoreOwnerIdOrderByIdDesc(Long bookOwnerId, Long storeOwnerId);
  @EntityGraph(attributePaths = {"book", "store"})
  List<BookPrice> findByBookId(Long bookId);
  @EntityGraph(attributePaths = {"book", "store"})
  List<BookPrice> findByStoreId(Long storeId);
  @EntityGraph(attributePaths = {"book", "store"})
  Optional<BookPrice> findByBookIdAndStoreId(Long bookId, Long storeId);

  @Override
  @EntityGraph(attributePaths = {"book", "store"})
  Optional<BookPrice> findById(Long id);
}
