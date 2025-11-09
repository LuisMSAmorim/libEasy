package br.com.amorimtech.loanservice.mapper;

import br.com.amorimtech.loanservice.dto.LoanRequest;
import br.com.amorimtech.loanservice.dto.LoanResponse;
import br.com.amorimtech.loanservice.model.Loan;

public class LoanMapper {

    public static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUserId(),
                loan.getBookId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }

    public static Loan toModel(LoanRequest request) {
        return new Loan(
                null,
                request.getUserId(),
                request.getBookId(),
                request.getLoanDate(),
                request.getDueDate(),
                request.getReturnDate(),
                request.getStatus()
        );
    }
}

