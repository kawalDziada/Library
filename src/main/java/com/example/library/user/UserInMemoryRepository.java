package com.example.library.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class UserInMemoryRepository implements UserRepository {
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
    public List<User> findAll(Sort sort) {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> users = new ArrayList<>(userStorage.values());
        return new PageImpl<>(users, pageable, users.size());
    }

    @Override
    public void flush() {}

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> users) {
        deleteAll(users);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {
        deleteAllById(ids);
    }

    @Override
    public User getOne(UUID id) {
        return getReferenceById(id);
    }

    @Override
    public User getById(UUID id) {
        return getReferenceById(id);
    }

    @Override
    public User getReferenceById(UUID id) {
        return userStorage.get(id);
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<S> savedUsers = new ArrayList<>();
        for (S user : entities) {
            save(user);
            savedUsers.add(user);
        }
        return savedUsers;
    }
}
