package br.com.amorimtech.libEasy.book.exception;


import br.com.amorimtech.libEasy.loan.exception.BookNotFoundForLoanException;
import br.com.amorimtech.libEasy.loan.exception.LoanNotFoundException;
import br.com.amorimtech.libEasy.loan.exception.UserNotFoundForLoanException;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookNotFoundException(BookNotFoundException exception) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, exception.getMessage())
                .createResponseEntity();
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleLoanNotFoundException(LoanNotFoundException exception) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, exception.getMessage())
                .createResponseEntity();
    }

    @ExceptionHandler(UserNotFoundForLoanException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundForLoanException(UserNotFoundForLoanException exception) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage())
                .createResponseEntity();
    }

    @ExceptionHandler(BookNotFoundForLoanException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookNotFoundForLoanException(BookNotFoundForLoanException exception) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage())
                .createResponseEntity();
    }
}
