package com.example.library.user;

import com.example.library.book.BookService;
import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new UserInMemoryRepository();
        userService = new UserService(userRepository, mock(BookService.class));
    }

    @Test
    void shouldGetAllUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        User user1 = new User(userId1, "John Doe");
        User user2 = new User(userId2, "Jane Doe");

        userRepository.save(user1);
        userRepository.save(user2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        UserDto dto1 = result.get(0);
        assertEquals(userId1, dto1.id());
        assertEquals("John Doe", dto1.name());

        UserDto dto2 = result.get(1);
        assertEquals(userId2, dto2.id());
        assertEquals("Jane Doe", dto2.name());
    }

    @Test
    void shouldGetUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");
        userRepository.save(user);

        UserDto result = userService.getUser(userId);

        assertEquals(userId, result.id());
        assertEquals("John Doe", result.name());
    }

    @Test
    void shouldCreateUser() {
        NewUserDto newUserDto = new NewUserDto("John Doe");

        UUID userId = userService.createUser(newUserDto);

        assertNotNull(userId);
        User savedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getName());
    }

    @Test
    void shouldUnregisterUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");
        userRepository.save(user);

        userService.unregisterUser(userId);

        assertEquals(Optional.empty(), userRepository.findById(userId));
    }
}
