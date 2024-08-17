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
public class BookController { // controller doesn't have to be public, package-private is enough, also, package-private should be the default choice
    private final List<Map<String, Object>> books = new ArrayList<>(); // you created a controller but not DTOs,
    // even without the database, you should have here
    // private final List<BookEntryDto> books = new ArrayList<>();
    private int nextId = 1;

    @Autowired // do you know 3 different types of dependency injection? here - you used "field injection", there are also "constructor injection" and "setter injection"
    // the "best" one is injecting by constructor, you can define constructor yourself, but it's better to take advantage of lombok's @RequiredArgsConstructor to the class (it would do the same a @Autowired)
    // if you're curious why constructor injection is the best, you can read about it, or we can talk about in during f2f
    @Lazy // why did you use this annotation? I've never seen it used in such context
    private UserController userController;

    @GetMapping
    public List<Map<String, Object>> getAllBooks() {
        return books;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getBookById(@PathVariable int id) { // it should return BookDto
        // it's very nice that you used streams, at this early stage of learning java
        return books.stream()
                .filter(book -> book.get("id").equals(id)) // in java, we never use maps in this context, as objects, instead you would have a BookDto, you would have book.getId(), in general using raw stings is something rather to avoid
                .findFirst()
                .orElse(null); // here you could throw an exception, then you can create an @ExceptionHandler,
        // it will be responsible for catching exceptions and returning correct HTTP Code (404 in this case), you can find reference in OspInternalController in lfc service
    }

    @PostMapping("/add")
    public String createBook(@RequestBody Map<String, Object> bookData) { // here request body should be NewBookDto
        Map<String, Object> newBook = new HashMap<>(bookData);
        newBook.put("id", nextId++);
        newBook.put("isAvailable", true); // here you have a warning, that this string literal is used 5 times, and should be a constant,
        // you'll replace map with BookDto, so it doesn't matter, but in general this is something you should do to avoid using raw Strings, especially if it's used 2 times or more
        books.add(newBook);
        return "Book " + newBook.get("name") + " registered with id " + newBook.get("id"); // here what you're returning is more of a log than a return value, same with other methods return values
        // think about what would be returned to frontend while creating a new something,
        // usually we would an id of created object, or you could also return BookDto

        // for logging, you can add annotation @Slf4j to class and put
        // log.info("Created new book {} with id {}", newBook.getName(), newBook.getId());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id) { // delete should be a void method
        Map<String, Object> bookToRemove = books.stream()
                .filter(book -> book.get("id").equals(id))
                .findFirst()
                .orElse(null); // this 4 lines are the same as in getBookById, you could extract this as a private method

        if (bookToRemove != null) {
            books.remove(bookToRemove);
            return "Book \"" + bookToRemove.get("name") + "\" was removed successfully";
        } else {
            return "Book not found"; // throw exception instead, maybe NoSuchElementException?
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
            return "Book not found or not available"; // again - exception, this time, maybe create a custom exception class, as this is a domain-specific exceptional situation
            // also, if you have an "and" or an "or" in method name, error message, log, it's usually a sign to split it either to separate method or separate processing path,
            // it's a good practice as it follow Single Responsibility Principle (SRP), which is the first and most important SOLID principle, please google it if this term is new to you
            // here you should handle "not found" separately from "not available" sine for the user it is different if the book doesn't exist and when it unavailable
        }

        Map<String, Object> user = userController.getAllUsers().stream() // you can use getUserById right?
                // normally we wouldn't call another controller from controller, instead we would call a service or even better, a facade, but we don't have it introduced yet
                .filter(u -> u.get("id").equals(userId))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return "User not found"; // here you don't need to get the user you just need to check if they exist, so once we add UserService, you could have method like userService.doesUserExists(userId);
        }
        // also a case to throw an exception

        bookToBorrow.put("isAvailable", false);
        bookToBorrow.put("borrowedBy", userId);
        return "Book \"" + bookToBorrow.get("name") + "\" was borrowed by user with id " + userId; // this method is very long, it would be best to split it in parts following SRP, like 1. getting the book, 2. getting the user,
        // and then you would move the "borrowing" logic to a BookDto (and later the Book entity), and you would call it like
        // book.markBorrowed(userId);
    }

    @PatchMapping("/return")
    public String returnBook(@RequestParam int id, @RequestParam int userId) { // this should also be a void method
        Map<String, Object> bookToReturn = books.stream()
                .filter(book -> book.get("borrowedBy").equals(userId) && book.get("id").equals(id))
                .findFirst()
                .orElse(null); // it would be better to first find the book, throw exception if it doesn't exist
        // then do book.isBorrowedBy(userId), if not, throw exception with message like "Book ... is not borrowed by user ..."
        // and there do book.release(), which would remove userId and set available to true

        if (bookToReturn == null) {
            return "Book not found or not available";
        }

        bookToReturn.put("isAvailable", true);
        bookToReturn.remove("borrowedBy");
        return "Book \"" + bookToReturn.get("name") + "\" was returned by user with id " + userId;
    }
}
