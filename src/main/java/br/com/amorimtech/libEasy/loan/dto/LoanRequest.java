package br.com.amorimtech.libEasy.loan.dto;

import br.com.amorimtech.libEasy.loan.model.LoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Loan date is required")
    private LocalDate loanDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDate returnDate;

    @NotNull(message = "Status is required")
    private LoanStatus status;
}
