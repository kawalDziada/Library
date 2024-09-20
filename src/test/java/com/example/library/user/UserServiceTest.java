package com.example.library.user;

import com.example.library.book.BookService;
import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        User user1 = new User(userId1, "John Doe");
        User user2 = new User(userId2, "Jane Doe");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        UserDto dto1 = result.get(0);
        assertEquals(userId1, dto1.id());
        assertEquals("John Doe", dto1.name());

        UserDto dto2 = result.get(1);
        assertEquals(userId2, dto2.id());
        assertEquals("Jane Doe", dto2.name());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertEquals(userId, result.id());
        assertEquals("John Doe", result.name());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser() {
        NewUserDto newUserDto = new NewUserDto("John Doe");
        User user = new User(UUID.randomUUID(), "John Doe");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UUID userId = userService.createUser(newUserDto);

        assertNotNull(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void unregisterUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.unregisterUser(userId);

        verify(bookService, times(1)).releaseAllForUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}
