package br.com.amorimtech.loanservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${gateway.url}")
    private String gatewayUrl;

    /**
     * Verifica se um livro existe chamando o book-service via gateway
     * @param bookId ID do livro
     * @return true se o livro existe, false caso contrário
     */
    public boolean bookExists(UUID bookId) {
        try {
            log.debug("Verificando existência do livro {} via gateway", bookId);
            
            WebClient webClient = webClientBuilder.baseUrl(gatewayUrl).build();
            
            Boolean exists = webClient.get()
                    .uri("/api/books/{id}", bookId)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.OK)) {
                            log.debug("Livro {} encontrado", bookId);
                            return Mono.just(true);
                        } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            log.debug("Livro {} não encontrado", bookId);
                            return Mono.just(false);
                        } else {
                            log.warn("Resposta inesperada ao verificar livro {}: {}", bookId, response.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
            
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Erro ao verificar existência do livro {} via gateway: {}", bookId, e.getMessage());
            // Em caso de erro de comunicação, retornamos false para evitar criar loans inválidos
            return false;
        }
    }
}

