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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void getAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setName("Book One");
        book1.setAuthor("Author One");
        book1.setAvailable(true);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Book Two");
        book2.setAuthor("Author Two");
        book2.setAvailable(false);

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
    void getBookById() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("123456789");
        book.setName("Book One");
        book.setAuthor("Author One");
        book.setPageNumber(300);
        book.setPublishDate(LocalDate.of(2020, 1, 1));
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
    void createBook() {
        NewBookDto newBookDto = new NewBookDto("123456789", "New Book", "New Author", 150, LocalDate.of(2021, 6, 15));
        Book book = Book.ofNew(newBookDto);
        book.setId(1L);

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
    void deleteBook() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void borrowBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);
        UUID userId = UUID.randomUUID();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.borrowBook(1L, userId);

        assertFalse(book.isAvailable());
        assertEquals(userId, book.getBorrowedBy());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void returnBook() {
        UUID userId = UUID.randomUUID();
        Book book = new Book();
        book.setId(1L);
        book.setBorrowedBy(userId);
        book.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.returnBook(1L, userId);

        assertTrue(book.isAvailable());
        assertNull(book.getBorrowedBy());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void releaseAllForUser() {
        UUID userId = UUID.randomUUID();
        Book book1 = new Book();
        book1.setId(1L);
        book1.setBorrowedBy(userId);
        book1.setAvailable(false);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setBorrowedBy(userId);
        book2.setAvailable(false);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

        bookService.releaseAllForUser(userId);

        assertTrue(book1.isAvailable());
        assertTrue(book2.isAvailable());
        verify(bookRepository, times(2)).save(any(Book.class));
    }
}
