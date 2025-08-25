package br.com.amorimtech.libEasy.book.exception;


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
}
