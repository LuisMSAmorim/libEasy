package br.com.amorimtech.libEasy.book.service;

import br.com.amorimtech.libEasy.book.exception.BookNotFoundException;
import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        book.setId(1L);
        book.setTitle("Dom Casmurro");
        book.setAuthor("Machado de Assis");
        book.setDescription("Romance Classico");
        book.setEditionNumber(1);
        book.setPublicationYear(1899);
    }

    @Test
    @DisplayName("Should find all books paginated")
    void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<Book> result = bookService.findAll(pageable);

        assertThat(result.getContent().size()).isEqualTo(1);
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find a book by ID")
    void testFindById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Dom Casmurro");
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw an exception when not found a book")
    void testFindByIdNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should create a new book")
    void testCreate() {
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.create(book);

        assertThat(result.getId()).isEqualTo(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Should update an existent book")
    void testUpdate() {
        Book updatedData = new Book();
        updatedData.setTitle("Novo título");
        updatedData.setAuthor("Novo autor");
        updatedData.setDescription("Nova descrição");
        updatedData.setEditionNumber(2);
        updatedData.setPublicationYear(2024);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.update(1L, updatedData);

        assertThat(result.getTitle()).isEqualTo("Novo título");
        assertThat(result.getEditionNumber()).isEqualTo(2);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Should delete a book")
    void testDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.delete(1L);

        verify(bookRepository, times(1)).delete(book);
    }
}
