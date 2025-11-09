package br.com.amorimtech.loanservice.dto;

import br.com.amorimtech.loanservice.model.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LoanResponse {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
}

