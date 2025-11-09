package br.com.amorimtech.libEasy.loan.exception;

import java.util.UUID;

public class BookNotFoundForLoanException extends RuntimeException {
    public BookNotFoundForLoanException(UUID bookId) {
        super("Book with id " + bookId + " not found for loan operation");
    }
}
