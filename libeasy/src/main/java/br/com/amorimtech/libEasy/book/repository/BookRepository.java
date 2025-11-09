package br.com.amorimtech.libEasy.book.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import br.com.amorimtech.libEasy.book.model.Book;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
