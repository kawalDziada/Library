package com.example.library.user;

import com.example.library.book.BookService;
import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookService bookService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        try {
            UserDto user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/register")
    public String createUser(@RequestBody String name) {
        UUID userId = userService.createUser(new NewUserDto(name));
        return "User " + name + " registered with id " + userId;
    }

    @DeleteMapping("/unregister/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        try {
            bookService.getAllBooks().stream()
                    .map(book -> bookService.getBookById(book.getId()))
                    .filter(book -> book.getBorrowedBy() != null && book.getBorrowedBy().equals(id))
                    .forEach(book -> bookService.returnBook(book.getId(), id));

            userService.deleteUser(id);

            return ResponseEntity.ok("User with ID " + id + " unregistered and all borrowed books returned.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
        }
    }
}
