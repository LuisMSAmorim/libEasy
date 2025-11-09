package br.com.amorimtech.loanservice.exception;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(UUID id) {
        super("Loan with id " + id + " not found");
    }
}

