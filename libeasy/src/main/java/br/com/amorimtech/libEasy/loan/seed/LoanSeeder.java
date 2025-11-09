package br.com.amorimtech.libEasy.loan.seed;

import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import br.com.amorimtech.libEasy.loan.model.Loan;
import br.com.amorimtech.libEasy.loan.model.LoanStatus;
import br.com.amorimtech.libEasy.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class LoanSeeder implements CommandLineRunner {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    // User IDs created by auth-service DevAdminSeeder
    // 1 = Admin, 2 = João Silva, 3 = Maria Santos, 4 = Pedro Oliveira, 5 = Ana Costa, 6 = Carlos Ferreira
    private static final List<Long> USER_IDS = List.of(2L, 3L, 4L, 5L, 6L);

    @Override
    public void run(String... args) {
        if (loanRepository.count() > 0) {
            log.info("Empréstimos já existem no banco. Pulando seed.");
            return;
        }

        log.info("Iniciando seed de empréstimos...");

        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            log.warn("⚠️  Não há livros para criar empréstimos");
            return;
        }

        log.info("Usando IDs de usuários criados pelo auth-service: {}", USER_IDS);

        List<Loan> loans = new ArrayList<>();

        // Empréstimos ATIVOS (ACTIVE)
        if (books.size() > 0) {
            loans.add(Loan.builder()
                    .bookId(books.get(0).getId())
                    .userId(USER_IDS.get(0))  // João Silva
                    .loanDate(LocalDate.now().minusDays(5))
                    .dueDate(LocalDate.now().plusDays(9))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        if (books.size() > 1) {
            loans.add(Loan.builder()
                    .bookId(books.get(1).getId())
                    .userId(USER_IDS.get(1))  // Maria Santos
                    .loanDate(LocalDate.now().minusDays(3))
                    .dueDate(LocalDate.now().plusDays(11))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        if (books.size() > 6) {
            loans.add(Loan.builder()
                    .bookId(books.get(6).getId())
                    .userId(USER_IDS.get(2))  // Pedro Oliveira
                    .loanDate(LocalDate.now().minusDays(1))
                    .dueDate(LocalDate.now().plusDays(13))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        // Empréstimos ATRASADOS (LATE)
        if (books.size() > 2) {
            loans.add(Loan.builder()
                    .bookId(books.get(2).getId())
                    .userId(USER_IDS.get(3))  // Ana Costa
                    .loanDate(LocalDate.now().minusDays(20))
                    .dueDate(LocalDate.now().minusDays(6))
                    .status(LoanStatus.LATE)
                    .build());
        }

        if (books.size() > 10) {
            loans.add(Loan.builder()
                    .bookId(books.get(10).getId())
                    .userId(USER_IDS.get(4))  // Carlos Ferreira
                    .loanDate(LocalDate.now().minusDays(25))
                    .dueDate(LocalDate.now().minusDays(11))
                    .status(LoanStatus.LATE)
                    .build());
        }

        // Empréstimos DEVOLVIDOS (RETURNED)
        if (books.size() > 3) {
            loans.add(Loan.builder()
                    .bookId(books.get(3).getId())
                    .userId(USER_IDS.get(0))  // João Silva
                    .loanDate(LocalDate.now().minusDays(30))
                    .dueDate(LocalDate.now().minusDays(16))
                    .returnDate(LocalDate.now().minusDays(18))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 4) {
            loans.add(Loan.builder()
                    .bookId(books.get(4).getId())
                    .userId(USER_IDS.get(1))  // Maria Santos
                    .loanDate(LocalDate.now().minusDays(45))
                    .dueDate(LocalDate.now().minusDays(31))
                    .returnDate(LocalDate.now().minusDays(29))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 5) {
            loans.add(Loan.builder()
                    .bookId(books.get(5).getId())
                    .userId(USER_IDS.get(2))  // Pedro Oliveira
                    .loanDate(LocalDate.now().minusDays(50))
                    .dueDate(LocalDate.now().minusDays(36))
                    .returnDate(LocalDate.now().minusDays(30))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 12) {
            loans.add(Loan.builder()
                    .bookId(books.get(12).getId())
                    .userId(USER_IDS.get(3))  // Ana Costa
                    .loanDate(LocalDate.now().minusDays(60))
                    .dueDate(LocalDate.now().minusDays(46))
                    .returnDate(LocalDate.now().minusDays(47))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        loanRepository.saveAll(loans);
        log.info("{} empréstimos criados com sucesso!", loans.size());
        
        long activeLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.ACTIVE).count();
        long lateLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.LATE).count();
        long returnedLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.RETURNED).count();
        
        log.info("Status dos empréstimos:");
        log.info("   - {} ATIVOS", activeLoans);
        log.info("   - {} ATRASADOS", lateLoans);
        log.info("   - {} DEVOLVIDOS", returnedLoans);
    }
}
