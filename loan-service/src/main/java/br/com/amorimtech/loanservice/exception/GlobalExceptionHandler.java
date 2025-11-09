package br.com.amorimtech.loanservice.exception;


import br.com.amorimtech.loanservice.shared.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleLoanNotFoundException(LoanNotFoundException exception) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, exception.getMessage())
                .createResponseEntity();
    }

    @ExceptionHandler(BookNotFoundForLoanException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookNotFoundForLoanException(BookNotFoundForLoanException exception) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage())
                .createResponseEntity();
    }
}

