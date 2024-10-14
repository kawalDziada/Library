package com.example.library.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.library.user.dto.UserDto;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class User {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    static User of(String name) {
        return new User(null, name);
    }

    public UserDto mapToDto() {
        return new UserDto(id, name);
    }
}
