package br.com.amorimtech.bookservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BookResponse {
    private UUID id;
    private String title;
    private String description;
    private String author;
    private Integer editionNumber;
    private Integer publicationYear;
}

