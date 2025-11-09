package br.com.amorimtech.libEasy.loan.repository;

import br.com.amorimtech.libEasy.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Page<Loan> findByUserId(UUID userId, Pageable pageable);
}
