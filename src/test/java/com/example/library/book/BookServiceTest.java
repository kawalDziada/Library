package com.example.library.book;

import com.example.library.book.dto.BookDto;
import com.example.library.book.dto.BookEntryDto;
import com.example.library.book.dto.NewBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllBooks() {
        Book book1 = new Book(1L, "123456789", "Book One", "Author One", 200, LocalDate.of(2020, 1, 1), true);
        Book book2 = new Book(2L, "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookEntryDto> result = bookService.getAllBooks();

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

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldGetBookById() {
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookById(1L);

        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getIsbn());
        assertEquals("Book One", result.getName());
        assertEquals("Author One", result.getAuthor());
        assertEquals(300, result.getPageNumber());
        assertEquals(LocalDate.of(2020, 1, 1), result.getPublishDate());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateBook() {
        NewBookDto newBookDto = new NewBookDto("123456789", "New Book", "New Author", 150, LocalDate.of(2021, 6, 15));
        Book book = Book.ofNew(newBookDto);

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            savedBook.setId(1L);
            return savedBook;
        });

        Long bookId = bookService.createBook(newBookDto);

        assertEquals(1L, bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void shouldDeleteBook() {
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void shouldBorrowBook() {
        UUID userId = UUID.randomUUID();
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.borrowBook(1L, userId);

        assertFalse(book.isAvailable());
        assertEquals(userId, book.getBorrowedBy());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void shouldReturnBook() {
        UUID userId = UUID.randomUUID();
        Book book = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false);
        book.markBorrowed(userId);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.returnBook(1L, userId);

        assertTrue(book.isAvailable());
        assertNull(book.getBorrowedBy());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void shouldReleaseAllForUser() {
        UUID userId = UUID.randomUUID();
        Book book1 = new Book(1L, "123456789", "Book One", "Author One", 300, LocalDate.of(2020, 1, 1), false);
        book1.markBorrowed(userId);

        Book book2 = new Book(2L, "987654321", "Book Two", "Author Two", 150, LocalDate.of(2021, 6, 15), false);
        book2.markBorrowed(userId);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

        bookService.releaseAllForUser(userId);

        assertTrue(book1.isAvailable());
        assertTrue(book2.isAvailable());
        verify(bookRepository, times(2)).save(any(Book.class));
    }
}
