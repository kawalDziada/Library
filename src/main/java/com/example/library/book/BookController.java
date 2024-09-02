package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookEntryDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping("/add")
    public String createBook(@RequestBody NewBookDto newBookDto) {
        Long bookId = bookService.createBook(newBookDto);
        return "Book " + newBookDto.getName() + " registered with ID " + bookId;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "Book with ID " + id + " was deleted.";
    }

    @PatchMapping("/borrow")
    public String borrowBook(@RequestParam Long id, @RequestParam UUID userId) {
        bookService.borrowBook(id, userId);
        return "Book with ID " + id + " was borrowed by user with ID " + userId;
    }

    @PatchMapping("/return")
    public String returnBook(@RequestParam Long id, @RequestParam UUID userId) {
        bookService.returnBook(id, userId);
        return "Book with ID " + id + " was returned by user with ID " + userId;
    }
}
