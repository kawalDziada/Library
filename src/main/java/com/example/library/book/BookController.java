package com.example.library.book;

import com.example.library.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {
    private final List<Map<String, Object>> books = new ArrayList<>();
    private int nextId = 1;

    @Autowired
    @Lazy
    private UserController userController;

    @GetMapping
    public List<Map<String, Object>> getAllBooks() {
        return books;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getBookById(@PathVariable int id) {
        return books.stream()
                .filter(book -> book.get("id").equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping("/add")
    public String createBook(@RequestBody Map<String, Object> bookData) {
        Map<String, Object> newBook = new HashMap<>(bookData);
        newBook.put("id", nextId++);
        newBook.put("isAvailable", true);
        books.add(newBook);
        return "Book " + newBook.get("name") + " registered with id " + newBook.get("id");
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id) {
        Map<String, Object> bookToRemove = books.stream()
                .filter(book -> book.get("id").equals(id))
                .findFirst()
                .orElse(null);

        if (bookToRemove != null) {
            books.remove(bookToRemove);
            return "Book \"" + bookToRemove.get("name") + "\" was removed successfully";
        } else {
            return "Book not found";
        }
    }

    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableBooks() {
        return books.stream()
                .filter(book -> Boolean.TRUE.equals(book.get("isAvailable")))
                .toList();
    }

    @PatchMapping("/borrow")
    public String borrowBook(@RequestParam int id, @RequestParam int userId) {
        Map<String, Object> bookToBorrow = books.stream()
                .filter(book -> book.get("id").equals(id) && Boolean.TRUE.equals(book.get("isAvailable")))
                .findFirst()
                .orElse(null);

        if (bookToBorrow == null) {
            return "Book not found or not available";
        }

        Map<String, Object> user = userController.getAllUsers().stream()
                .filter(u -> u.get("id").equals(userId))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        bookToBorrow.put("isAvailable", false);
        bookToBorrow.put("borrowedBy", userId);
        return "Book \"" + bookToBorrow.get("name") + "\" was borrowed by user with id " + userId;
    }

    @PatchMapping("/return")
    public String returnBook(@RequestParam int id, @RequestParam int userId) {
        Map<String, Object> bookToReturn = books.stream()
                .filter(book -> book.get("borrowedBy").equals(userId) && book.get("id").equals(id))
                .findFirst()
                .orElse(null);

        if (bookToReturn == null) {
            return "Book not found or not available";
        }

        bookToReturn.put("isAvailable", true);
        bookToReturn.remove("borrowedBy");
        return "Book \"" + bookToReturn.get("name") + "\" was returned by user with id " + userId;
    }
}
