package br.com.amorimtech.bookservice.mapper;

import br.com.amorimtech.bookservice.dto.BookRequest;
import br.com.amorimtech.bookservice.dto.BookResponse;
import br.com.amorimtech.bookservice.model.Book;

public class BookMapper {

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getAuthor(),
                book.getEditionNumber(),
                book.getPublicationYear()
        );
    }

    public static Book toModel(BookRequest request) {
        return new Book(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getAuthor(),
                request.getEditionNumber(),
                request.getPublicationYear()
        );
    }
}

