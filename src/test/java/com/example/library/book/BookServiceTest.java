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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(result).hasSize(2);

        BookEntryDto dto1 = result.stream().findFirst().orElseThrow();
        assertThat(dto1.getId()).isEqualTo(1L);
        assertThat(dto1.getName()).isEqualTo("Book One");
        assertThat(dto1.getAuthor()).isEqualTo("Author One");
        assertThat(dto1.isAvailable()).isTrue();

        BookEntryDto dto2 = result.get(1);
        assertThat(dto2.getId()).isEqualTo(2L);
        assertThat(dto2.getName()).isEqualTo("Book Two");
        assertThat(dto2.getAuthor()).isEqualTo("Author Two");
        assertThat(dto2.isAvailable()).isFalse();
    }

    @Test
    void shouldGetBookById() {
        // given
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);
        bookRepository.save(book);

        // when
        BookDto result = bookService.getBookById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIsbn()).isEqualTo("123456789");
        assertThat(result.getName()).isEqualTo("Book One");
        assertThat(result.getAuthor()).isEqualTo("Author One");
        assertThat(result.getPageNumber()).isEqualTo(300);
        assertThat(result.getPublishDate()).isEqualTo(LocalDate.of(2020, 1, 1));
    }

    @Test
    void shouldCreateBook() {
        // given
        NewBookDto newBookDto = new NewBookDto("123456789", "New Book", "New Author", 150, LocalDate.of(2021, 6, 15));

        // when
        Long bookId = bookService.createBook(newBookDto);

        // then
        Book savedBook = bookRepository.findById(bookId).orElseThrow();
        assertThat(savedBook.getId()).isEqualTo(1L);
        assertThat(savedBook.getIsbn()).isEqualTo("123456789");
        assertThat(savedBook.getName()).isEqualTo("New Book");
        assertThat(savedBook.getAuthor()).isEqualTo("New Author");
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
        assertThat(deletedBook).isEmpty();
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
        assertThat(borrowedBook.isAvailable()).isFalse();
        assertThat(borrowedBook.getBorrowedBy()).isEqualTo(userId);
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
        assertThat(returnedBook.isAvailable()).isTrue();
        assertThat(returnedBook.getBorrowedBy()).isNull();
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
        assertThat(bookRepository.findById(1L).orElseThrow().isAvailable()).isTrue();
        assertThat(bookRepository.findById(2L).orElseThrow().isAvailable()).isTrue();
    }
}