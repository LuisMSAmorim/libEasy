package br.com.amorimtech.libEasy.book.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String description;
    private String author;
    private Integer editionNumber;
    private Integer publicationYear;
}
