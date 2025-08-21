package br.com.amorimtech.libEasy.book.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import br.com.amorimtech.libEasy.book.domain.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
