package br.com.amorimtech.libEasy.book.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Book {
    private final BookId id;
    private final String title;
    private final String description;
    private final String author;
    @JsonProperty("ISSN")
    private final Issn issn;
    private final int editionNumber;
    private final int publicationYear;

    public Book(BookId id, String title, String description, String author, Issn issn, int editionNumber, int publicationYear) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }
        if (editionNumber < 1) {
            throw new IllegalArgumentException("Edition number cannot be less than 1");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.issn = issn;
        this.editionNumber = editionNumber;
        this.publicationYear = publicationYear;
    }
}
