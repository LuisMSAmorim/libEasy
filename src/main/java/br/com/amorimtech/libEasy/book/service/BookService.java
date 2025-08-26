package br.com.amorimtech.libEasy.book.service;


import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import br.com.amorimtech.libEasy.book.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Page<Book> findAll(Pageable pageable)  {
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book bookData) {
        Book book = this.findById(id);

        book.setTitle(bookData.getTitle());
        book.setAuthor(bookData.getAuthor());
        book.setDescription(bookData.getDescription());
        book.setEditionNumber(bookData.getEditionNumber());
        book.setPublicationYear(bookData.getPublicationYear());

        return bookRepository.save(book);
    }
}

