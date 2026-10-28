package ru.urfu.lab8.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab8.dto.BookForm;
import ru.urfu.lab8.dto.StoreForm;
import ru.urfu.lab8.dto.PriceForm;
import ru.urfu.lab8.entity.*;
import ru.urfu.lab8.exception.*;
import ru.urfu.lab8.repository.*;

@Service
@Transactional(readOnly = true)
public class BookCatalogService {
  private final BookRepository books;
  private final BookstoreRepository stores;
  private final BookPriceRepository prices;
  private final UserAccountRepository users;

  public BookCatalogService(BookRepository books, BookstoreRepository stores,
      BookPriceRepository prices, UserAccountRepository users) {
    this.books = books; this.stores = stores; this.prices = prices; this.users = users;
  }

  private boolean admin(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }
  private boolean member(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
  }
  private UserAccount current(Authentication a) {
    return users.findByEmailIgnoreCase(a.getName())
        .orElseThrow(() -> new AccessDeniedException("Пользователь не найден"));
  }
  private void ownerOnly(UserAccount owner, Authentication a) {
    if (!admin(a) && !Objects.equals(owner.getId(), current(a).getId()))
      throw new AccessDeniedException("Запись принадлежит другому пользователю");
  }
  private void canWrite(Authentication a) {
    if (!admin(a) && !member(a)) throw new AccessDeniedException("Недостаточно прав");
  }
  private boolean seesEverything(Authentication a) { return admin(a) || !member(a); }

  public List<Book> books(Authentication a) {
    return seesEverything(a) ? books.findAllByOrderByIdDesc()
        : books.findByOwnerIdOrderByIdDesc(current(a).getId());
  }
  public List<Bookstore> stores(Authentication a) {
    return seesEverything(a) ? stores.findAllByOrderByIdDesc()
        : stores.findByOwnerIdOrderByIdDesc(current(a).getId());
  }
  public List<BookPrice> prices(Authentication a) {
    return seesEverything(a) ? prices.findAllByOrderByIdDesc()
        : prices.findByBookOwnerIdAndStoreOwnerIdOrderByIdDesc(current(a).getId(), current(a).getId());
  }
  public Book book(long id, Authentication a) {
    Book b = books.findById(id).orElseThrow(() -> new NotFoundException("Книга не найдена"));
    if (member(a)) ownerOnly(b.getOwner(), a);
    return b;
  }
  public Bookstore store(long id, Authentication a) {
    Bookstore s = stores.findById(id).orElseThrow(() -> new NotFoundException("Магазин не найден"));
    if (member(a)) ownerOnly(s.getOwner(), a);
    return s;
  }
  public BookPrice price(long id, Authentication a) {
    BookPrice p = prices.findById(id).orElseThrow(() -> new NotFoundException("Цена не найдена"));
    if (member(a)) { ownerOnly(p.getBook().getOwner(), a); ownerOnly(p.getStore().getOwner(), a); }
    return p;
  }

  @Transactional
  public Book addBook(BookForm f, Authentication a) {
    canWrite(a);
    return books.save(new Book(f.getTitle(), f.getAuthor(), f.getPublicationYear(), current(a)));
  }
  @Transactional
  public Book editBook(long id, BookForm f, Authentication a) {
    canWrite(a); Book b = book(id, a); ownerOnly(b.getOwner(), a);
    b.update(f.getTitle(), f.getAuthor(), f.getPublicationYear()); return b;
  }
  @Transactional
  public void removeBook(long id, Authentication a) {
    canWrite(a); Book b = book(id, a); ownerOnly(b.getOwner(), a);
    prices.deleteAll(prices.findByBookId(id)); books.delete(b);
  }
  @Transactional
  public Bookstore addStore(StoreForm f, Authentication a) {
    canWrite(a);
    return stores.save(new Bookstore(f.getName(), f.getAddress(), current(a)));
  }
  @Transactional
  public Bookstore editStore(long id, StoreForm f, Authentication a) {
    canWrite(a); Bookstore s = store(id, a); ownerOnly(s.getOwner(), a);
    s.update(f.getName(), f.getAddress()); return s;
  }
  @Transactional
  public void removeStore(long id, Authentication a) {
    canWrite(a); Bookstore s = store(id, a); ownerOnly(s.getOwner(), a);
    prices.deleteAll(prices.findByStoreId(id)); stores.delete(s);
  }
  @Transactional
  public BookPrice addPrice(PriceForm f, Authentication a) {
    canWrite(a); Book b = book(f.getBookId(), a); Bookstore s = store(f.getStoreId(), a);
    ownerOnly(b.getOwner(), a); ownerOnly(s.getOwner(), a);
    if (prices.findByBookIdAndStoreId(b.getId(), s.getId()).isPresent())
      throw new ConflictException("Цена этой книги в этом магазине уже указана");
    return prices.save(new BookPrice(b, s, f.getPrice()));
  }
  @Transactional
  public BookPrice editPrice(long id, PriceForm f, Authentication a) {
    canWrite(a); BookPrice p = price(id, a);
    Book b = book(f.getBookId(), a); Bookstore s = store(f.getStoreId(), a);
    ownerOnly(b.getOwner(), a); ownerOnly(s.getOwner(), a);
    var duplicate = prices.findByBookIdAndStoreId(b.getId(), s.getId());
    if (duplicate.isPresent() && !duplicate.get().getId().equals(id))
      throw new ConflictException("Цена этой книги в этом магазине уже указана");
    p.update(b, s, f.getPrice()); return p;
  }
  @Transactional
  public void removePrice(long id, Authentication a) {
    canWrite(a); prices.delete(price(id, a));
  }
  public BigDecimal calculate(long priceId, int quantity, Authentication a) {
    if (quantity < 1 || quantity > 10000) throw new IllegalArgumentException("Количество от 1 до 10000");
    return price(priceId, a).getPrice().multiply(BigDecimal.valueOf(quantity));
  }
}
