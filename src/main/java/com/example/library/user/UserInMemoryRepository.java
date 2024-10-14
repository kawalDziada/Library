package com.example.library.user;

import com.example.library.utils.BaseInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserInMemoryRepository extends BaseInMemoryRepository<User, UUID> implements UserRepository {
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
    public boolean existsById(UUID id) {
        return userStorage.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public List<User> findAllById(Iterable<UUID> ids) {
        List<User> users = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(users::add));
        return users;
    }

    @Override
    public long count() {
        return userStorage.size();
    }

    @Override
    public void deleteById(UUID id) {
        userStorage.remove(id);
    }

    @Override
    public void delete(User entity) {
        userStorage.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        ids.forEach(userStorage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(user -> userStorage.remove(user.getId()));
    }

    @Override
    public void deleteAll() {
        userStorage.clear();
    }

    @Override
    public void flush() {}

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        S savedEntity = save(entity);
        flush();
        return savedEntity;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        List<S> savedEntities = saveAll(entities);
        flush();
        return savedEntities;
    }

    @Override
    public User getOne(UUID id) {
        return getById(id);
    }

    @Override
    public User getById(UUID id) {
        return userStorage.get(id);
    }

    @Override
    public User getReferenceById(UUID id) {
        return getById(id);
    }
}
