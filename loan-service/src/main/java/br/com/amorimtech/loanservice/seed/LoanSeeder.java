package br.com.amorimtech.loanservice.seed;

import br.com.amorimtech.loanservice.model.Loan;
import br.com.amorimtech.loanservice.model.LoanStatus;
import br.com.amorimtech.loanservice.repository.LoanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient.Builder webClientBuilder;

    @Value("${gateway.url}")
    private String gatewayUrl;

    // User UUIDs que devem corresponder aos criados pelo auth-service
    private static final List<UUID> USER_IDS = List.of(
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        UUID.fromString("00000000-0000-0000-0000-000000000003"),
        UUID.fromString("00000000-0000-0000-0000-000000000004"),
        UUID.fromString("00000000-0000-0000-0000-000000000005"),
        UUID.fromString("00000000-0000-0000-0000-000000000006")
    );

    @Override
    public void run(String... args) {
        if (loanRepository.count() > 0) {
            log.info("Empréstimos já existem no banco. Pulando seed.");
            return;
        }

        log.info("Iniciando seed de empréstimos...");
        
        // Buscar livros do book-service com retry
        List<UUID> bookIds = fetchBookIdsWithRetry();
        
        if (bookIds.isEmpty()) {
            log.warn("⚠️  Nenhum livro encontrado no book-service após várias tentativas");
            log.warn("⚠️  Pulando seed de empréstimos");
            log.warn("⚠️  Verifique se o book-service está rodando e tem livros cadastrados");
            return;
        }
        
        List<Loan> loans = new ArrayList<>();

        // Empréstimos ATIVOS (ACTIVE)
        if (bookIds.size() > 0) {
            loans.add(Loan.builder()
                    .bookId(bookIds.get(0))
                    .userId(USER_IDS.get(0))
                    .loanDate(LocalDate.now().minusDays(5))
                    .dueDate(LocalDate.now().plusDays(9))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        if (bookIds.size() > 1) {
            loans.add(Loan.builder()
                    .bookId(bookIds.get(1))
                    .userId(USER_IDS.get(1))
                    .loanDate(LocalDate.now().minusDays(3))
                    .dueDate(LocalDate.now().plusDays(11))
                    .status(LoanStatus.ACTIVE)
                    .build());
        }

        // Empréstimos ATRASADOS (LATE)
        if (bookIds.size() > 2) {
            loans.add(Loan.builder()
                    .bookId(bookIds.get(2))
                    .userId(USER_IDS.get(3))
                    .loanDate(LocalDate.now().minusDays(20))
                    .dueDate(LocalDate.now().minusDays(6))
                    .status(LoanStatus.LATE)
                    .build());
        }

        // Empréstimos DEVOLVIDOS (RETURNED)
        if (bookIds.size() > 3) {
            loans.add(Loan.builder()
                    .bookId(bookIds.get(3))
                    .userId(USER_IDS.get(0))
                    .loanDate(LocalDate.now().minusDays(30))
                    .dueDate(LocalDate.now().minusDays(16))
                    .returnDate(LocalDate.now().minusDays(18))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        if (bookIds.size() > 4) {
            loans.add(Loan.builder()
                    .bookId(bookIds.get(4))
                    .userId(USER_IDS.get(1))
                    .loanDate(LocalDate.now().minusDays(45))
                    .dueDate(LocalDate.now().minusDays(31))
                    .returnDate(LocalDate.now().minusDays(29))
                    .status(LoanStatus.RETURNED)
                    .build());
        }

        loanRepository.saveAll(loans);
        log.info("✅ {} empréstimos criados com sucesso!", loans.size());
        
        long activeLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.ACTIVE).count();
        long lateLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.LATE).count();
        long returnedLoans = loans.stream().filter(l -> l.getStatus() == LoanStatus.RETURNED).count();
        
        log.info("📊 Status dos empréstimos:");
        log.info("   - {} ATIVOS", activeLoans);
        log.info("   - {} ATRASADOS", lateLoans);
        log.info("   - {} DEVOLVIDOS", returnedLoans);
    }

    /**
     * Busca IDs de livros com retry logic
     */
    private List<UUID> fetchBookIdsWithRetry() {
        int maxRetries = 10;
        int retryDelay = 3000; // 3 segundos
        
        for (int i = 1; i <= maxRetries; i++) {
            log.info("Tentativa {}/{} de buscar livros do book-service...", i, maxRetries);
            
            List<UUID> bookIds = fetchBookIdsFromBookService();
            if (!bookIds.isEmpty()) {
                return bookIds;
            }
            
            if (i < maxRetries) {
                try {
                    log.info("Aguardando {} segundos antes da próxima tentativa...", retryDelay / 1000);
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        return List.of();
    }

    /**
     * Busca IDs de livros do book-service diretamente (sem passar pelo gateway para evitar autenticação)
     */
    private List<UUID> fetchBookIdsFromBookService() {
        try {
            // Conectar diretamente ao book-service (não via gateway, para evitar autenticação)
            WebClient webClient = webClientBuilder.baseUrl("http://book-service:8080").build();
            
            JsonNode response = webClient.get()
                    .uri("/books?size=20")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null && response.has("data") && response.get("data").has("content")) {
                JsonNode content = response.get("data").get("content");
                List<UUID> bookIds = new ArrayList<>();
                
                content.forEach(book -> {
                    if (book.has("id")) {
                        bookIds.add(UUID.fromString(book.get("id").asText()));
                    }
                });
                
                log.info("✅ {} livros encontrados!", bookIds.size());
                return bookIds;
            }
            
            return List.of();
        } catch (Exception e) {
            log.debug("Erro ao buscar livros do book-service: {}", e.getMessage());
            return List.of();
        }
    }
}

