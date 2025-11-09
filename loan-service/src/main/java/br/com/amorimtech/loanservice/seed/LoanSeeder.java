package br.com.amorimtech.loanservice.seed;

import br.com.amorimtech.loanservice.model.Loan;
import br.com.amorimtech.loanservice.model.LoanStatus;
import br.com.amorimtech.loanservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class LoanSeeder implements CommandLineRunner {

    private final LoanRepository loanRepository;

    // User UUIDs que devem corresponder aos criados pelo auth-service
    private static final List<UUID> USER_IDS = List.of(
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        UUID.fromString("00000000-0000-0000-0000-000000000003"),
        UUID.fromString("00000000-0000-0000-0000-000000000004"),
        UUID.fromString("00000000-0000-0000-0000-000000000005"),
        UUID.fromString("00000000-0000-0000-0000-000000000006")
    );

    // NOTA: Em um ambiente de microsserviços, IDs de livros devem ser obtidos do book-service
    // Para desenvolvimento, você precisará:
    // 1. Executar o book-service primeiro para criar os livros
    // 2. Obter os IDs dos livros criados
    // 3. Substituir os UUIDs abaixo pelos IDs reais
    // 
    // Alternativamente, você pode:
    // - Criar os empréstimos manualmente via API após o sistema estar rodando
    // - Implementar um cliente HTTP para buscar os livros do book-service durante o seed
    private static final List<UUID> BOOK_IDS = List.of(
        UUID.fromString("11111111-1111-1111-1111-111111111111"),
        UUID.fromString("22222222-2222-2222-2222-222222222222"),
        UUID.fromString("33333333-3333-3333-3333-333333333333"),
        UUID.fromString("44444444-4444-4444-4444-444444444444"),
        UUID.fromString("55555555-5555-5555-5555-555555555555"),
        UUID.fromString("66666666-6666-6666-6666-666666666666"),
        UUID.fromString("77777777-7777-7777-7777-777777777777")
    );

    @Override
    public void run(String... args) {
        if (loanRepository.count() > 0) {
            log.info("Empréstimos já existem no banco. Pulando seed.");
            return;
        }

        log.warn("⚠️  LoanSeeder está desabilitado em ambiente de microsserviços");
        log.warn("⚠️  Os IDs de livros precisam ser obtidos do book-service primeiro");
        log.warn("⚠️  Crie empréstimos manualmente via API após o sistema estar rodando");
        log.warn("⚠️  Ou configure os BOOK_IDS com IDs reais do book-service");

        // Comentado para evitar erros de referência a livros inexistentes
        /*
        log.info("Iniciando seed de empréstimos...");

        List<Loan> loans = new ArrayList<>();

        // Empréstimos ATIVOS (ACTIVE)
        loans.add(Loan.builder()
                .bookId(BOOK_IDS.get(0))
                .userId(USER_IDS.get(0))
                .loanDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(9))
                .status(LoanStatus.ACTIVE)
                .build());

        loans.add(Loan.builder()
                .bookId(BOOK_IDS.get(1))
                .userId(USER_IDS.get(1))
                .loanDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(11))
                .status(LoanStatus.ACTIVE)
                .build());

        // Empréstimos ATRASADOS (LATE)
        loans.add(Loan.builder()
                .bookId(BOOK_IDS.get(2))
                .userId(USER_IDS.get(3))
                .loanDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(6))
                .status(LoanStatus.LATE)
                .build());

        // Empréstimos DEVOLVIDOS (RETURNED)
        loans.add(Loan.builder()
                .bookId(BOOK_IDS.get(3))
                .userId(USER_IDS.get(0))
                .loanDate(LocalDate.now().minusDays(30))
                .dueDate(LocalDate.now().minusDays(16))
                .returnDate(LocalDate.now().minusDays(18))
                .status(LoanStatus.RETURNED)
                .build());

        loanRepository.saveAll(loans);
        log.info("{} empréstimos criados com sucesso!", loans.size());
        
        long activeLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.ACTIVE).count();
        long lateLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.LATE).count();
        long returnedLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.RETURNED).count();
        
        log.info("Status dos empréstimos:");
        log.info("   - {} ATIVOS", activeLoans);
        log.info("   - {} ATRASADOS", lateLoans);
        log.info("   - {} DEVOLVIDOS", returnedLoans);
        */
    }
}

