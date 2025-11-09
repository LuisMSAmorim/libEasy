package br.com.amorimtech.libEasy.loan.seed;

import br.com.amorimtech.libEasy.auth.model.User;
import br.com.amorimtech.libEasy.auth.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (loanRepository.count() > 0) {
            log.info("Empréstimos já existem no banco. Pulando seed.");
            return;
        }

        log.info("Iniciando seed de empréstimos...");

        List<User> users = userRepository.findAll();
        List<Book> books = bookRepository.findAll();

        if (users.isEmpty() || books.isEmpty()) {
            log.warn("⚠️  Não há usuários ou livros para criar empréstimos");
            return;
        }

        users = users.stream()
                .filter(u -> u.getEmail().contains("@email.com"))
                .toList();

        List<Loan> loans = new ArrayList<>();

        if (books.size() > 0 && users.size() > 0) {
            loans.add(Loan.builder()
                    .bookId(books.get(0).getId())
                    .userId(users.get(0).getId())
                    .loanDate(LocalDate.now().minusDays(5))
                    .dueDate(LocalDate.now().plusDays(9))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        if (books.size() > 1 && users.size() > 1) {
            loans.add(Loan.builder()
                    .bookId(books.get(1).getId())
                    .userId(users.get(1).getId())
                    .loanDate(LocalDate.now().minusDays(3))
                    .dueDate(LocalDate.now().plusDays(11))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        if (books.size() > 6 && users.size() > 2) {
            loans.add(Loan.builder()
                    .bookId(books.get(6).getId())
                    .userId(users.get(2).getId())
                    .loanDate(LocalDate.now().minusDays(1))
                    .dueDate(LocalDate.now().plusDays(13))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        // Empréstimos ATRASADOS (LATE)
        if (books.size() > 2 && users.size() > 3) {
            loans.add(Loan.builder()
                    .bookId(books.get(2).getId())
                    .userId(users.get(3).getId())
                    .loanDate(LocalDate.now().minusDays(20))
                    .dueDate(LocalDate.now().minusDays(6))
                    .status(LoanStatus.LATE)
                    .build());
        }

        if (books.size() > 10 && users.size() > 4) {
            loans.add(Loan.builder()
                    .bookId(books.get(10).getId())
                    .userId(users.get(4).getId())
                    .loanDate(LocalDate.now().minusDays(25))
                    .dueDate(LocalDate.now().minusDays(11))
                    .status(LoanStatus.LATE)
                    .build());
        }

        // Empréstimos DEVOLVIDOS (RETURNED)
        if (books.size() > 3 && users.size() > 0) {
            loans.add(Loan.builder()
                    .bookId(books.get(3).getId())
                    .userId(users.get(0).getId())
                    .loanDate(LocalDate.now().minusDays(30))
                    .dueDate(LocalDate.now().minusDays(16))
                    .returnDate(LocalDate.now().minusDays(18))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 4 && users.size() > 1) {
            loans.add(Loan.builder()
                    .bookId(books.get(4).getId())
                    .userId(users.get(1).getId())
                    .loanDate(LocalDate.now().minusDays(45))
                    .dueDate(LocalDate.now().minusDays(31))
                    .returnDate(LocalDate.now().minusDays(29))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 5 && users.size() > 2) {
            loans.add(Loan.builder()
                    .bookId(books.get(5).getId())
                    .userId(users.get(2).getId())
                    .loanDate(LocalDate.now().minusDays(50))
                    .dueDate(LocalDate.now().minusDays(36))
                    .returnDate(LocalDate.now().minusDays(30))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (books.size() > 12 && users.size() > 3) {
            loans.add(Loan.builder()
                    .bookId(books.get(12).getId())
                    .userId(users.get(3).getId())
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
