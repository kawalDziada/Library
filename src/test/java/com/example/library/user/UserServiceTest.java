package com.example.library.user;

import com.example.library.book.BookService;
import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        User user1 = new User(userId1, "John Doe");
        User user2 = new User(userId2, "Jane Doe");

        userRepository.save(user1);
        userRepository.save(user2);

        // when
        List<UserDto> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(2);

        UserDto dto1 = result.stream().filter(dto -> dto.id().equals(userId1)).findFirst().orElseThrow();
        assertThat(dto1.id()).isEqualTo(userId1);
        assertThat(dto1.name()).isEqualTo("John Doe");

        UserDto dto2 = result.stream().filter(dto -> dto.id().equals(userId2)).findFirst().orElseThrow();
        assertThat(dto2.id()).isEqualTo(userId2);
        assertThat(dto2.name()).isEqualTo("Jane Doe");
    }

    @Test
    void shouldGetUser() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");
        userRepository.save(user);

        // when
        UserDto result = userService.getUser(userId);

        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("John Doe");
    }

    @Test
    void shouldCreateUser() {
        // given
        NewUserDto newUserDto = new NewUserDto("John Doe");

        // when
        UUID userId = userService.createUser(newUserDto);

        // then
        assertThat(userId).isNotNull();

        User savedUser = userRepository.findById(userId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldUnregisterUser() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe");
        userRepository.save(user);

        // when
        userService.unregisterUser(userId);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
