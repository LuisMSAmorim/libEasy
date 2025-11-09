package br.com.amorimtech.bookservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message= "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Edition number is required")
    @Positive(message = "Edition number must be greater than 0")
    private Integer editionNumber;

    @NotNull(message = "Publication year is required")
    private Integer publicationYear;
}

