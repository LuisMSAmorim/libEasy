package br.com.amorimtech.libEasy.loan.repository;

import br.com.amorimtech.libEasy.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
