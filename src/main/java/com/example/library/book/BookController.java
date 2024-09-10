package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookEntryDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public String createBook(@RequestBody NewBookDto newBookDto) {
        Long bookId = bookService.createBook(newBookDto);
        return "Book " + newBookDto.getName() + " registered with ID " + bookId;
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "Book with ID " + id + " was deleted.";
    }

    @PatchMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestParam Long id, @RequestParam UUID userId) {
        try {
            bookService.borrowBook(id, userId);
            return ResponseEntity.ok("Book with ID " + id + " was borrowed by user with ID " + userId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam Long id, @RequestParam UUID userId) {
        try {
            bookService.returnBook(id, userId);
            return ResponseEntity.ok("Book with ID " + id + " was returned by user with ID " + userId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
