package br.com.amorimtech.libEasy.book.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;  // ✔ CORRETO
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String author;

    @JsonProperty("ISSN")
    @Pattern(regexp = "^\\d{4}-\\d{3}[\\dX]$", message = "ISSN must be in the format 1234-567X")
    @Column(nullable = false)
    private String issn;

    @Column(nullable = false)
    private int editionNumber;

    @Column(nullable = false)
    private int publicationYear;
}
