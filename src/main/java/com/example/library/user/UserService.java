package com.example.library.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    public UserDto getUser(UUID userId) {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User with id %s doesn't exist".formatted(userId)));
//        return user.mapToDto(); // it would be better with a mapper but for now, let's try this way (to not overcomplicate things from the start)
//    }

//    public final UUID createUser(NewUserDto newUser) {
//        var createdUser = userRepository.save(User.of(newUser.getName()));
//        return createdUser.getId();
//    }
}
