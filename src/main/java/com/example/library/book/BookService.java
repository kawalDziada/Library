package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private static class BookNotFoundException extends NoSuchElementException {
        private BookNotFoundException(Long id) {
            super("Book not found with ID " + id);
        }
    }

    List<BookEntryDto> getAllBooks() {
        return bookRepository.findAll().stream()
            .map(this::mapToEntryDto)
            .toList();
    }

    BookDto getBookById(Long id) {
        return bookRepository.findById(id)
            .map(this::mapToDto)
            .orElseThrow(() -> new BookNotFoundException(id));
    }

    Long createBook(NewBookDto newBookDto) {
        Book book = Book.ofNew(newBookDto);
        bookRepository.save(book);
        return book.getId();
    }

    void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }

    void borrowBook(Long id, UUID userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        if (!book.isAvailable()) {
            throw new IllegalStateException("The book with ID " + id + " is already borrowed.");
        }
        book.markBorrowed(userId);
        bookRepository.save(book);
    }

    void returnBook(Long id, UUID userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        if (!book.isBorrowedBy(userId)) {
            throw new IllegalStateException("The book with ID " + id + " was not borrowed by user with ID " + userId);
        }
        book.release();
        bookRepository.save(book);
    }

    public void releaseAllForUser(UUID userId) {
        bookRepository.findAll().stream()
                .filter(book -> book.getBorrowedBy() != null && book.getBorrowedBy().equals(userId))
                .forEach(book -> returnBook(book.getId(), userId));
    }

    private BookEntryDto mapToEntryDto(Book book) {
        return new BookEntryDto(
            book.getId(),
            book.getName(),
            book.getAuthor(),
            book.isAvailable()
        );
    }

    private BookDto mapToDto(Book book) {
        return new BookDto(
            book.getId(),
            book.getIsbn(),
            book.getName(),
            book.getAuthor(),
            book.getPageNumber(),
            book.getPublishDate(),
            book.isAvailable(),
            book.getBorrowedBy()
        );
    }
}
