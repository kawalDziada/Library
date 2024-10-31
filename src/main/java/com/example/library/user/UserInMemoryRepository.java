package com.example.library.user;

import com.example.library.utils.BaseInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class UserInMemoryRepository extends BaseInMemoryRepository<User, UUID> implements UserRepository {
    private final Map<UUID, User> userStorage = new ConcurrentHashMap<>();

    @Override
    public <S extends User> S save(S entity) {
        if (entity.getId() == null) {
            UUID generatedId = UUID.randomUUID();
            entity = (S) new User(generatedId, entity.getName());
        }
        userStorage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public void deleteById(UUID id) {
        userStorage.remove(id);
    }

    @Override
    public void deleteAll() {
        userStorage.clear();
    }
}
