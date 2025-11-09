package br.com.amorimtech.libEasy.loan.service;


import br.com.amorimtech.libEasy.auth.model.Role;
import br.com.amorimtech.libEasy.auth.model.User;
import br.com.amorimtech.libEasy.auth.repository.UserRepository;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import br.com.amorimtech.libEasy.loan.exception.BookNotFoundForLoanException;
import br.com.amorimtech.libEasy.loan.exception.UserNotFoundForLoanException;
import br.com.amorimtech.libEasy.loan.model.Loan;
import br.com.amorimtech.libEasy.loan.repository.LoanRepository;
import br.com.amorimtech.libEasy.loan.exception.LoanNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public Page<Loan> findAllForUser(User currentUser, Pageable pageable) {
        if (currentUser.getRole() == Role.ADMIN) {
            return loanRepository.findAll(pageable);
        } else {
            return loanRepository.findByUserId(currentUser.getId(), pageable);
        }
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    public Loan findByIdForUser(Long id, User currentUser) {
        Loan loan = findById(id);
        
        if (currentUser.getRole() != Role.ADMIN && !loan.getUserId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own loans");
        }
        
        return loan;
    }

    public Loan create(Loan loan) {
        validateUserExists(loan.getUserId());
        validateBookExists(loan.getBookId());
        return loanRepository.save(loan);
    }

    public Loan createForUser(Loan loan, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN && !loan.getUserId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create loans for yourself");
        }
        
        return create(loan);
    }

    public Loan update(Long id, Loan loanData) {
        Loan loan = this.findById(id);

        validateUserExists(loanData.getUserId());
        validateBookExists(loanData.getBookId());

        loan.setUserId(loanData.getUserId());
        loan.setBookId(loanData.getBookId());
        loan.setLoanDate(loanData.getLoanDate());
        loan.setDueDate(loanData.getDueDate());
        loan.setReturnDate(loanData.getReturnDate());
        loan.setStatus(loanData.getStatus());

        return loanRepository.save(loan);
    }

    public void delete(Long id) {
        Loan loan = this.findById(id);
        loanRepository.delete(loan);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundForLoanException(userId);
        }
    }

    private void validateBookExists(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundForLoanException(bookId);
        }
    }
}
