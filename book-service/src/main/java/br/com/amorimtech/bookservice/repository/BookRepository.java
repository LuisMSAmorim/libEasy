package br.com.amorimtech.bookservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import br.com.amorimtech.bookservice.model.Book;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}

