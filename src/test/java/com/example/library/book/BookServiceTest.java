package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookServiceTest {

    private final BookRepository bookRepository = new BookInMemoryRepository();
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    private final BookService bookService = new BookService(bookRepository, bookMapper);

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldGetAllBooks() {
        // given
        Book book1 = new Book(UUID.randomUUID(), "123456789", "Book One", "Author One", 200, LocalDate.of(2020, 1, 1), true, null);
        Book book2 = new Book(UUID.randomUUID(), "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false, null);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // when
        List<BookEntryDto> result = bookService.getAllBooks();

        // then
        assertThat(result).hasSize(2);

        BookEntryDto dto1 = result.get(0);
        BookEntryDto dto2 = result.get(1);

        assertThat(dto1)
                .returns(book1.getId(), BookEntryDto::getId)
                .returns(book1.getName(), BookEntryDto::getName)
                .returns(book1.getAuthor(), BookEntryDto::getAuthor)
                .returns(book1.isAvailable(), BookEntryDto::isAvailable);

        assertThat(dto2)
                .returns(book2.getId(), BookEntryDto::getId)
                .returns(book2.getName(), BookEntryDto::getName)
                .returns(book2.getAuthor(), BookEntryDto::getAuthor)
                .returns(book2.isAvailable(), BookEntryDto::isAvailable);
    }

    @Test
    void shouldGetBookById() {
        // given
        UUID bookId = UUID.randomUUID();
        Book book = new Book(bookId, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true, null);
        bookRepository.save(book);

        // when
        BookDto result = bookService.getBookById(bookId);

        // then
        assertThat(result)
                .returns(book.getId(), BookDto::getId)
                .returns(book.getIsbn(), BookDto::getIsbn)
                .returns(book.getName(), BookDto::getName)
                .returns(book.getAuthor(), BookDto::getAuthor)
                .returns(book.getPageNumber(), BookDto::getPageNumber)
                .returns(book.getPublishDate(), BookDto::getPublishDate);
    }

    @Test
    void shouldCreateBook() {
        // given
        NewBookDto newBookDto = new NewBookDto("123456789", "New Book", "New Author", 150, LocalDate.of(2021, 6, 15));

        // when
        UUID bookId = bookService.createBook(newBookDto);

        // then
        Book savedBook = bookRepository.findById(bookId).orElseThrow();
        assertThat(savedBook)
                .returns(bookId, Book::getId)
                .returns("123456789", Book::getIsbn)
                .returns("New Book", Book::getName)
                .returns("New Author", Book::getAuthor);
    }

    @Test
    void shouldDeleteBook() {
        // given
        UUID bookId = UUID.randomUUID();
        Book book = new Book(bookId, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true, null);
        bookRepository.save(book);

        // when
        bookService.deleteBook(bookId);

        // then
        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook).isEmpty();
    }

    @Test
    void shouldBorrowBook() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Book book = new Book(bookId, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true, null);
        bookRepository.save(book);

        // when
        bookService.borrowBook(bookId, userId);

        // then
        Book borrowedBook = bookRepository.findById(bookId).orElseThrow();
        assertThat(borrowedBook)
                .returns(false, Book::isAvailable)
                .returns(userId, Book::getBorrowedBy);
    }

    @Test
    void shouldReturnBook() {
        // given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Book book = new Book(bookId, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false, userId);
        bookRepository.save(book);

        // when
        bookService.returnBook(bookId, userId);

        // then
        Book returnedBook = bookRepository.findById(bookId).orElseThrow();
        assertThat(returnedBook)
                .returns(true, Book::isAvailable)
                .returns(null, Book::getBorrowedBy);
    }

    @Test
    void shouldReleaseAllForUser() {
        // given
        UUID userId = UUID.randomUUID();
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();
        Book book1 = new Book(bookId1, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false, userId);
        Book book2 = new Book(bookId2, "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false, userId);
        bookRepository.save(book1);
        bookRepository.save(book2);

        // when
        bookService.releaseAllForUser(userId);

        // then
        assertThat(bookRepository.findById(bookId1).orElseThrow())
                .returns(true, Book::isAvailable)
                .returns(null, Book::getBorrowedBy);
        assertThat(bookRepository.findById(bookId2).orElseThrow())
                .returns(true, Book::isAvailable)
                .returns(null, Book::getBorrowedBy);
    }
}
