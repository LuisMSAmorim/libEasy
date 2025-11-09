package br.com.amorimtech.libEasy.book.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "books")
public class BookDocument {
    
    @Id
    private UUID id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String author;
    
    @Field(type = FieldType.Integer)
    private Integer editionNumber;
    
    @Field(type = FieldType.Integer)
    private Integer publicationYear;
}
