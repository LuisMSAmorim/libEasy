package br.com.amorimtech.libEasy.book.domain.model;

public record BookId(Long value) {
    public BookId{
        if (value == null) {
            throw new IllegalArgumentException("BookId cannot be null");
        }
    }
}
