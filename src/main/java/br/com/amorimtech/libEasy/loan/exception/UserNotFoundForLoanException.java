package br.com.amorimtech.libEasy.loan.exception;

public class UserNotFoundForLoanException extends RuntimeException {
    public UserNotFoundForLoanException(Long userId) {
        super("User with id " + userId + " not found for loan operation");
    }
}
