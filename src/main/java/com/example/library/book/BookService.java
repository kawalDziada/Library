package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<BookEntryDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToEntryDto)
                .toList();
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("Book not found with ID " + id));
    }

    public Long createBook(NewBookDto newBookDto) {
        Book book = new Book();
        book.setIsbn(newBookDto.getIsbn());
        book.setName(newBookDto.getName());
        book.setAuthor(newBookDto.getAuthor());
        book.setPageNumber(newBookDto.getPageNumber());
        book.setPublishDate(newBookDto.getPublishDate());
        bookRepository.save(book);
        return book.getId();
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with ID " + id));
        bookRepository.delete(book);
    }

    public void borrowBook(Long id, UUID userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found or not available with ID " + id));
        if (!book.isAvailable()) {
            throw new IllegalStateException("The book with ID " + id + " is already borrowed.");
        }
        book.markBorrowed(userId);
        bookRepository.save(book);
    }

    public void returnBook(Long id, UUID userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with ID " + id));
        if (!book.getBorrowedBy().equals(userId)) {
            throw new IllegalStateException("The book with ID " + id + " was not borrowed by user with ID " + userId);
        }
        book.release();
        bookRepository.save(book);
    }

    private BookEntryDto mapToEntryDto(Book book) {
        BookEntryDto dto = new BookEntryDto();
        dto.setId(book.getId());
        dto.setName(book.getName());
        dto.setAuthor(book.getAuthor());
        dto.setAvailable(book.isAvailable());
        return dto;
    }

    private BookDto mapToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setName(book.getName());
        dto.setAuthor(book.getAuthor());
        dto.setPageNumber(book.getPageNumber());
        dto.setPublishDate(book.getPublishDate());
        dto.setAvailable(book.isAvailable());
        dto.setBorrowedBy(book.getBorrowedBy());
        return dto;
    }
}
