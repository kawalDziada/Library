package com.example.library.book;

import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BookInMemoryRepository implements BookRepository {
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
    public List<Book> findAll(Sort sort) {
        return new ArrayList<>(bookStorage.values());
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        List<Book> books = new ArrayList<>(bookStorage.values());
        return new PageImpl<>(books, pageable, books.size());
    }

    @Override
    public void flush() {}

    @Override
    public <S extends Book> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Book> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public void deleteAllInBatch(Iterable<Book> books) {
        deleteAll(books);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        deleteAllById(ids);
    }

    @Override
    public Book getOne(Long id) {
        return getReferenceById(id);
    }

    @Override
    public Book getById(Long id) {
        return getReferenceById(id);
    }

    @Override
    public Book getReferenceById(Long id) {
        return bookStorage.get(id);
    }

    @Override
    public <S extends Book> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Book> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Book> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Book> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends Book> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Book> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Book, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        List<S> savedBooks = new ArrayList<>();
        for (S book : entities) {
            save(book);
            savedBooks.add(book);
        }
        return savedBooks;
    }
}
