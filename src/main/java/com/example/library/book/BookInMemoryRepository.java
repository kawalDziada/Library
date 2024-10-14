package com.example.library.book;

import com.example.library.utils.BaseInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BookInMemoryRepository extends BaseInMemoryRepository<Book, Long> implements BookRepository {
    private final Map<Long, Book> bookStorage = new ConcurrentHashMap<>();
    private long currentId = 1L;

    @Override
    public <S extends Book> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(currentId++);
        }
        bookStorage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(bookStorage.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return bookStorage.containsKey(id);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(bookStorage.values());
    }

    @Override
    public List<Book> findAllById(Iterable<Long> ids) {
        List<Book> books = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(books::add));
        return books;
    }

    @Override
    public long count() {
        return bookStorage.size();
    }

    @Override
    public void deleteById(Long id) {
        bookStorage.remove(id);
    }

    @Override
    public void delete(Book entity) {
        bookStorage.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(bookStorage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends Book> entities) {
        entities.forEach(book -> bookStorage.remove(book.getId()));
    }

    @Override
    public void deleteAll() {
        bookStorage.clear();
    }

    @Override
    public void flush() {}

    @Override
    public <S extends Book> S saveAndFlush(S entity) {
        S savedEntity = save(entity);
        flush();
        return savedEntity;
    }

    @Override
    public <S extends Book> List<S> saveAllAndFlush(Iterable<S> entities) {
        List<S> savedEntities = saveAll(entities);
        flush();
        return savedEntities;
    }

    @Override
    public Book getOne(Long id) {
        return getById(id);
    }

    @Override
    public Book getById(Long id) {
        return bookStorage.get(id);
    }

    @Override
    public Book getReferenceById(Long id) {
        return getById(id);
    }
}
