package br.com.amorimtech.libEasy.loan.service;


import br.com.amorimtech.libEasy.book.repository.BookRepository;
import br.com.amorimtech.libEasy.loan.exception.BookNotFoundForLoanException;
import br.com.amorimtech.libEasy.loan.model.Loan;
import br.com.amorimtech.libEasy.loan.repository.LoanRepository;
import br.com.amorimtech.libEasy.loan.exception.LoanNotFoundException;
import br.com.amorimtech.libEasy.shared.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public Page<Loan> findAllForUser(UserDTO currentUser, Pageable pageable) {
        if (currentUser.isAdmin()) {
            return loanRepository.findAll(pageable);
        } else {
            return loanRepository.findByUserId(currentUser.getId(), pageable);
        }
    }

    public Loan findById(UUID id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    public Loan findByIdForUser(UUID id, UserDTO currentUser) {
        Loan loan = findById(id);

        if (!currentUser.isAdmin() && !loan.getUserId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own loans");
        }

        return loan;
    }

    public Loan create(Loan loan) {
        // No user validation - Gateway already validated authentication
        validateBookExists(loan.getBookId());
        return loanRepository.save(loan);
    }

    public Loan createForUser(Loan loan, UserDTO currentUser) {
        if (!currentUser.isAdmin() && !loan.getUserId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create loans for yourself");
        }

        return create(loan);
    }

    public Loan update(UUID id, Loan loanData) {
        Loan loan = this.findById(id);

        // No user validation - Gateway already validated authentication
        validateBookExists(loanData.getBookId());

        loan.setUserId(loanData.getUserId());
        loan.setBookId(loanData.getBookId());
        loan.setLoanDate(loanData.getLoanDate());
        loan.setDueDate(loanData.getDueDate());
        loan.setReturnDate(loanData.getReturnDate());
        loan.setStatus(loanData.getStatus());

        return loanRepository.save(loan);
    }

    public void delete(UUID id) {
        Loan loan = this.findById(id);
        loanRepository.delete(loan);
    }

    private void validateBookExists(UUID bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundForLoanException(bookId);
        }
    }
}
