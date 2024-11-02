package com.example.library.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface BookRepository extends JpaRepository<Book, UUID> {
}
