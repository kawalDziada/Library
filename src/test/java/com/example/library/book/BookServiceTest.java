package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = new BookInMemoryRepository();
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldGetAllBooks() {
        // given
        Book book1 = new Book(1L, "123456789", "Book One", "Author One", 200, LocalDate.of(2020, 1, 1), true);
        Book book2 = new Book(2L, "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // when
        List<BookEntryDto> result = bookService.getAllBooks();

        // then
        assertEquals(2, result.size());
        BookEntryDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Book One", dto1.getName());
        assertEquals("Author One", dto1.getAuthor());
        assertTrue(dto1.isAvailable());

        BookEntryDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Book Two", dto2.getName());
        assertEquals("Author Two", dto2.getAuthor());
        assertFalse(dto2.isAvailable());
    }

    @Test
    void shouldGetBookById() {
        // given
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);
        bookRepository.save(book);

        // when
        BookDto result = bookService.getBookById(1L);

        // then
        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getIsbn());
        assertEquals("Book One", result.getName());
        assertEquals("Author One", result.getAuthor());
        assertEquals(300, result.getPageNumber());
        assertEquals(LocalDate.of(2020, 1, 1), result.getPublishDate());
    }

    @Test
    void shouldCreateBook() {
        // given
        NewBookDto newBookDto = new NewBookDto("123456789", "New Book", "New Author", 150, LocalDate.of(2021, 6, 15));
        Book book = Book.ofNew(newBookDto);

        // when
        Long bookId = bookService.createBook(newBookDto);

        // then
        Book savedBook = bookRepository.findById(bookId).orElseThrow();
        assertEquals(1L, savedBook.getId());
        assertEquals("123456789", savedBook.getIsbn());
        assertEquals("New Book", savedBook.getName());
        assertEquals("New Author", savedBook.getAuthor());
    }

    @Test
    void shouldDeleteBook() {
        // given
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);
        bookRepository.save(book);

        // when
        bookService.deleteBook(1L);

        // then
        Optional<Book> deletedBook = bookRepository.findById(1L);
        assertTrue(deletedBook.isEmpty());
    }

    @Test
    void shouldBorrowBook() {
        // given
        UUID userId = UUID.randomUUID();
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);
        bookRepository.save(book);

        // when
        bookService.borrowBook(1L, userId);

        // then
        Book borrowedBook = bookRepository.findById(1L).orElseThrow();
        assertFalse(borrowedBook.isAvailable());
        assertEquals(userId, borrowedBook.getBorrowedBy());
    }

    @Test
    void shouldReturnBook() {
        // given
        UUID userId = UUID.randomUUID();
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false);
        book.markBorrowed(userId);
        bookRepository.save(book);

        // when
        bookService.returnBook(1L, userId);

        // then
        Book returnedBook = bookRepository.findById(1L).orElseThrow();
        assertTrue(returnedBook.isAvailable());
        assertNull(returnedBook.getBorrowedBy());
    }

    @Test
    void shouldReleaseAllForUser() {
        // given
        UUID userId = UUID.randomUUID();
        Book book1 = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false);
        book1.markBorrowed(userId);
        Book book2 = new Book(2L, "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false);
        book2.markBorrowed(userId);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // when
        bookService.releaseAllForUser(userId);

        // then
        assertTrue(bookRepository.findById(1L).orElseThrow().isAvailable());
        assertTrue(bookRepository.findById(2L).orElseThrow().isAvailable());
    }
}
