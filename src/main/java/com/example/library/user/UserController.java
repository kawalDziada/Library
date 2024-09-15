package com.example.library.user;

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

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @PostMapping("/register")
    public UUID createUser(@RequestBody String name) {
        UUID userId = userService.createUser(new NewUserDto(name));
        System.out.println("User " + name + " registered with id " + userId);
        return userId;
    }

    @DeleteMapping("/unregister/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.unregisterUser(id);
        return ResponseEntity.ok("User with ID " + id + " unregistered and all borrowed books returned.");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex) {
        return "An unexpected error occurred: " + ex.getMessage();
    }
}
