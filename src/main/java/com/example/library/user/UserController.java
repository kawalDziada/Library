package com.example.library.user;

import com.example.library.book.BookController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController { // same comments apply here
    private final List<Map<String, Object>> users = new ArrayList<>();
    private int nextId = 1;

    @Autowired
    @Lazy
    private BookController bookController;

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable int id) {
        return users.stream()
                .filter(user -> user.get("id").equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping("/register")
    public String createUser(@RequestBody String name) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("id", nextId++);
        newUser.put("name", name);
        users.add(newUser);
        return "User " + name + " registered with id " + newUser.get("id");
    }

    @DeleteMapping("/unregister/{id}")
    public String deleteUser(@PathVariable int id) {
        Map<String, Object> userToRemove = users.stream()
                .filter(user -> user.get("id").equals(id))
                .findFirst()
                .orElse(null);

        if (userToRemove != null) {
            List<Map<String, Object>> booksToReturn = bookController.getAllBooks().stream()
                    .filter(book -> book.get("borrowedBy") != null && book.get("borrowedBy").equals(id))
                    .toList();

            for (Map<String, Object> book : booksToReturn) { // modifying objects from other packages is a very bad practice,
                // this can lead to spaghetti code - meaning everything mixed whit everything, it's a very quick way to having unmaintainable code
                // instead use
                // booksToReturn.forEach(book -> bookController.returnBook(book.getId(), id));
                book.remove("borrowedBy");
                book.put("isAvailable", true);
            }

            users.remove(userToRemove);
            return "User " + userToRemove.get("name") + " unregistered and all borrowed books returned.";
        } else {
            return "User not found";
        }
    }
}
