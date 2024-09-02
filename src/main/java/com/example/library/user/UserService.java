package com.example.library.user;

import com.example.library.user.dto.NewUserDto;
import com.example.library.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(User::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(UUID userId) {
        return userRepository.findById(userId)
                .map(User::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("User with id %s doesn't exist".formatted(userId)));
    }

    public UUID createUser(NewUserDto newUser) {
        var createdUser = userRepository.save(User.of(newUser.name()));
        return createdUser.getId();
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id %s doesn't exist".formatted(userId)));
        userRepository.delete(user);
    }
}
