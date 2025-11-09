package br.com.amorimtech.libEasy.loan.exception;

public class BookNotFoundForLoanException extends RuntimeException {
    public BookNotFoundForLoanException(Long bookId) {
        super("Book with id " + bookId + " not found for loan operation");
    }
}
