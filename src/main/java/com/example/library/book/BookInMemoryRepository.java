package com.example.library.book;

import com.example.library.utils.BaseInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BookInMemoryRepository extends BaseInMemoryRepository<Book, UUID> implements BookRepository {
    private final Map<UUID, Book> bookStorage = new ConcurrentHashMap<>();

    @Override
    public <S extends Book> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        bookStorage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return Optional.ofNullable(bookStorage.get(id));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(bookStorage.values());
    }

    @Override
    public void delete(Book entity) {
        bookStorage.remove(entity.getId());
    }

    @Override
    public void deleteById(UUID id) {
        bookStorage.remove(id);
    }

    @Override
    public void deleteAll() {
        bookStorage.clear();
    }
}
