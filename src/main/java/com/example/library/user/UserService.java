package com.example.library.user;

import com.example.library.book.BookService;
import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;
    private final BookService bookService;

    List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(User::mapToDto)
            .toList();
    }

    UserDto getUser(UUID userId) {
        return userRepository.findById(userId)
            .map(User::mapToDto)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    UUID createUser(NewUserDto newUser) {
        User createdUser = userRepository.save(User.of(newUser.name()));
        return createdUser.getId();
    }

    void unregisterUser(UUID userId) {
        bookService.releaseAllForUser(userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userRepository.deleteById(userId);
    }
}
