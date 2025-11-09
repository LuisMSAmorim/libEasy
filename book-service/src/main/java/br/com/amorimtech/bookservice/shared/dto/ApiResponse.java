package br.com.amorimtech.bookservice.shared.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ApiResponse<T> {
    private boolean success;
    private T data;
    @JsonIgnore
    private HttpStatusCode status;
    private String errorMessage;

    public static <T> ApiResponse<T> success(T data, HttpStatusCode status) {
        return new ApiResponse<>(
                true,
                data,
                status,
                null
        );
    }

    public static <T> ApiResponse<T> error(HttpStatusCode status, String errorMessage) {
        return new ApiResponse<>(
                false,
                null,
                status,
                errorMessage
        );
    }

    public ResponseEntity<ApiResponse<T>> createResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }
}

