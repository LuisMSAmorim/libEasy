package br.com.amorimtech.libEasy.book.service;


import br.com.amorimtech.libEasy.book.document.BookDocument;
import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import br.com.amorimtech.libEasy.book.repository.BookSearchRepository;
import br.com.amorimtech.libEasy.book.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final BookSearchRepository bookSearchRepository;

    public Page<Book> findAll(Pageable pageable)  {
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        Book savedBook = bookRepository.save(book);
        
        try {
            indexBook(savedBook);
        } catch (Exception e) {
            log.error("Erro ao indexar livro no Elasticsearch: {}", e.getMessage());
        }
        
        return savedBook;
    }

    public Book update(Long id, Book bookData) {
        Book book = this.findById(id);

        book.setTitle(bookData.getTitle());
        book.setAuthor(bookData.getAuthor());
        book.setDescription(bookData.getDescription());
        book.setEditionNumber(bookData.getEditionNumber());
        book.setPublicationYear(bookData.getPublicationYear());

        Book updatedBook = bookRepository.save(book);
        
        try {
            indexBook(updatedBook);
        } catch (Exception e) {
            log.error("Erro ao atualizar livro no Elasticsearch: {}", e.getMessage());
        }
        
        return updatedBook;
    }

    public void delete(Long id) {
        Book book = this.findById(id);
        bookRepository.delete(book);
        
        try {
            bookSearchRepository.deleteById(id);
            log.info("Livro {} removido do índice Elasticsearch", id);
        } catch (Exception e) {
            log.error("Erro ao remover livro do Elasticsearch: {}", e.getMessage());
        }
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        log.info("Buscando livros com query: '{}'", query);
        
        Page<BookDocument> elasticsearchResults;
        if (query == null || query.trim().isEmpty()) {
            elasticsearchResults = bookSearchRepository.findAll(pageable);
        } else {
            elasticsearchResults = bookSearchRepository.searchByAllFields(query.trim(), pageable);
        }
        
        List<Long> bookIds = elasticsearchResults.getContent()
                .stream()
                .map(BookDocument::getId)
                .toList();
        
        if (bookIds.isEmpty()) {
            return Page.empty(pageable);
        }
        
        List<Book> books = bookRepository.findAllById(bookIds);
        
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));
        
        List<Book> orderedBooks = bookIds.stream()
                .map(bookMap::get)
                .filter(Objects::nonNull)
                .toList();
        
        return new PageImpl<>(
                orderedBooks,
                pageable,
                elasticsearchResults.getTotalElements()
        );
    }

    private void indexBook(Book book) {
        log.info("Indexando livro: {}", book.getTitle());
        
        BookDocument document = BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .editionNumber(book.getEditionNumber())
                .publicationYear(book.getPublicationYear())
                .build();
                
        bookSearchRepository.save(document);
        log.info("Livro indexado: {}", book.getTitle());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Aplicação iniciada. Iniciando reindexação dos livros...");
        try {
            bookSearchRepository.deleteAll();
            
            Iterable<Book> allBooks = bookRepository.findAll();
            allBooks.forEach(this::indexBook);
            
            log.info("Reindexação concluída com sucesso!");
        } catch (Exception e) {
            log.error("Erro durante a reindexação inicial: {}", e.getMessage());
        }
    }
}
