package br.com.amorimtech.libEasy.loan.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Loan with id " + id + " not found");
    }
}
