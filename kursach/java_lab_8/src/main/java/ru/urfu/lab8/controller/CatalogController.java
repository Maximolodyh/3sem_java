package ru.urfu.lab8.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.lab8.dto.*;
import ru.urfu.lab8.entity.*;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.service.ActionLogService;
import ru.urfu.lab8.service.BookCatalogService;

@Controller
public class CatalogController {
  private final BookCatalogService catalog;
  private final ActionLogService audit;
  public CatalogController(BookCatalogService catalog, ActionLogService audit) {
    this.catalog = catalog; this.audit = audit;
  }

  @GetMapping("/books")
  String books(Model m, Authentication a) {
    m.addAttribute("books", catalog.books(a)); return "books";
  }
  @GetMapping("/books/new")
  String bookNew(Model m) {
    m.addAttribute("bookForm", new BookForm()); m.addAttribute("action", "/books");
    return "book-form";
  }
  @PostMapping("/books")
  String bookCreate(@Valid @ModelAttribute BookForm bookForm, BindingResult b, Authentication a, Model m) {
    if (b.hasErrors()) { m.addAttribute("action", "/books"); return "book-form"; }
    Book book = catalog.addBook(bookForm, a);
    audit.record(a.getName(), "Добавлена книга #" + book.getId());
    return "redirect:/books";
  }
  @GetMapping("/books/{id}/edit")
  String bookEdit(@PathVariable long id, Authentication a, Model m) {
    Book book = catalog.book(id, a);
    BookForm f = new BookForm(); f.setTitle(book.getTitle()); f.setAuthor(book.getAuthor());
    f.setPublicationYear(book.getPublicationYear());
    m.addAttribute("bookForm", f); m.addAttribute("action", "/books/" + id);
    return "book-form";
  }
  @PostMapping("/books/{id}")
  String bookUpdate(@PathVariable long id, @Valid @ModelAttribute BookForm bookForm,
      BindingResult b, Authentication a, Model m) {
    if (b.hasErrors()) { m.addAttribute("action", "/books/" + id); return "book-form"; }
    catalog.editBook(id, bookForm, a);
    audit.record(a.getName(), "Изменена книга #" + id);
    return "redirect:/books";
  }
  @PostMapping("/books/{id}/delete")
  String bookDelete(@PathVariable long id, Authentication a) {
    catalog.removeBook(id, a); audit.record(a.getName(), "Удалена книга #" + id);
    return "redirect:/books";
  }

  @GetMapping("/bookstores")
  String stores(Model m, Authentication a) {
    m.addAttribute("stores", catalog.stores(a)); return "bookstores";
  }
  @GetMapping("/bookstores/new")
  String storeNew(Model m) {
    m.addAttribute("storeForm", new StoreForm()); m.addAttribute("action", "/bookstores");
    return "store-form";
  }
  @PostMapping("/bookstores")
  String storeCreate(@Valid @ModelAttribute StoreForm storeForm, BindingResult b, Authentication a, Model m) {
    if (b.hasErrors()) { m.addAttribute("action", "/bookstores"); return "store-form"; }
    Bookstore s = catalog.addStore(storeForm, a);
    audit.record(a.getName(), "Добавлен магазин #" + s.getId());
    return "redirect:/bookstores";
  }
  @GetMapping("/bookstores/{id}/edit")
  String storeEdit(@PathVariable long id, Authentication a, Model m) {
    Bookstore s = catalog.store(id, a);
    StoreForm f = new StoreForm(); f.setName(s.getName()); f.setAddress(s.getAddress());
    m.addAttribute("storeForm", f); m.addAttribute("action", "/bookstores/" + id);
    return "store-form";
  }
  @PostMapping("/bookstores/{id}")
  String storeUpdate(@PathVariable long id, @Valid @ModelAttribute StoreForm storeForm,
      BindingResult b, Authentication a, Model m) {
    if (b.hasErrors()) { m.addAttribute("action", "/bookstores/" + id); return "store-form"; }
    catalog.editStore(id, storeForm, a);
    audit.record(a.getName(), "Изменён магазин #" + id);
    return "redirect:/bookstores";
  }
  @PostMapping("/bookstores/{id}/delete")
  String storeDelete(@PathVariable long id, Authentication a) {
    catalog.removeStore(id, a); audit.record(a.getName(), "Удалён магазин #" + id);
    return "redirect:/bookstores";
  }

  @GetMapping("/prices")
  String prices(Model m, Authentication a, @RequestParam(required = false) String error) {
    m.addAttribute("prices", catalog.prices(a)); m.addAttribute("error", error);
    return "prices";
  }
  private void priceFormModel(Model m, Authentication a, String action) {
    m.addAttribute("books", catalog.books(a));
    m.addAttribute("stores", catalog.stores(a));
    m.addAttribute("action", action);
  }
  @GetMapping("/prices/new")
  String priceNew(Model m, Authentication a) {
    m.addAttribute("priceForm", new PriceForm()); priceFormModel(m, a, "/prices");
    return "price-form";
  }
  @PostMapping("/prices")
  String priceCreate(@Valid @ModelAttribute PriceForm priceForm, BindingResult b,
      Model m, Authentication a) {
    if (!b.hasErrors()) {
      try {
        BookPrice p = catalog.addPrice(priceForm, a);
        audit.record(a.getName(), "Добавлена цена #" + p.getId()); return "redirect:/prices";
      } catch (ConflictException e) { b.reject("conflict", e.getMessage()); }
    }
    priceFormModel(m, a, "/prices"); return "price-form";
  }
  @GetMapping("/prices/{id}/edit")
  String priceEdit(@PathVariable long id, Model m, Authentication a) {
    BookPrice p = catalog.price(id, a);
    PriceForm f = new PriceForm(); f.setBookId(p.getBook().getId());
    f.setStoreId(p.getStore().getId()); f.setPrice(p.getPrice());
    m.addAttribute("priceForm", f); priceFormModel(m, a, "/prices/" + id);
    return "price-form";
  }
  @PostMapping("/prices/{id}")
  String priceUpdate(@PathVariable long id, @Valid @ModelAttribute PriceForm priceForm,
      BindingResult b, Model m, Authentication a) {
    if (!b.hasErrors()) {
      try {
        catalog.editPrice(id, priceForm, a);
        audit.record(a.getName(), "Изменена цена #" + id); return "redirect:/prices";
      } catch (ConflictException e) { b.reject("conflict", e.getMessage()); }
    }
    priceFormModel(m, a, "/prices/" + id); return "price-form";
  }
  @PostMapping("/prices/{id}/delete")
  String priceDelete(@PathVariable long id, Authentication a) {
    catalog.removePrice(id, a); audit.record(a.getName(), "Удалена цена #" + id);
    return "redirect:/prices";
  }

  @GetMapping("/calculator")
  String calculator(Model m, Authentication a) {
    m.addAttribute("prices", catalog.prices(a)); return "calculator";
  }
  @PostMapping("/calculator")
  String calculate(@RequestParam(required = false) Long priceId,
      @RequestParam(defaultValue = "1") int quantity, Authentication a, Model m) {
    m.addAttribute("prices", catalog.prices(a));
    m.addAttribute("priceId", priceId);
    m.addAttribute("quantity", quantity);
    if (priceId == null || quantity < 1 || quantity > 10000) {
      m.addAttribute("error", "Выберите предложение и количество от 1 до 10000");
    } else {
      BigDecimal result = catalog.calculate(priceId, quantity, a);
      m.addAttribute("result", result);
      audit.record(a.getName(), "Расчёт стоимости: предложение #" + priceId + ", количество " + quantity);
    }
    return "calculator";
  }

  @GetMapping("/about")
  String about() { return "about"; }

  @GetMapping("/logs")
  String logs(Model m) {
    m.addAttribute("actions", audit.recent()); return "logs";
  }
}
