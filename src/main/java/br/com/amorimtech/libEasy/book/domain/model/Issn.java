package br.com.amorimtech.libEasy.book.domain.model;

public record Issn(String value) {
    public Issn {
        if (value == null || !value.matches("\\d{4}-\\d{3}[\\dxX]")){
            throw new IllegalArgumentException("Invalid ISSN format");
        }
    }
}
