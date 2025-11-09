package br.com.amorimtech.libEasy.book.seed;

import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class BookSeeder implements CommandLineRunner {
    
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            log.info("Livros já existem no banco. Pulando seed.");
            return;
        }

        log.info("Iniciando seed de livros...");

        List<Book> books = List.of(
            Book.builder()
                .title("Spring Boot in Action")
                .author("Craig Walls")
                .description("Um guia completo sobre Spring Boot e desenvolvimento de microservices modernos")
                .editionNumber(2)
                .publicationYear(2023)
                .build(),

            Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .description("Melhores práticas e padrões de programação Java essenciais")
                .editionNumber(3)
                .publicationYear(2018)
                .build(),

            Book.builder()
                .title("Java Concurrency in Practice")
                .author("Brian Goetz")
                .description("Guia definitivo sobre programação concorrente em Java")
                .editionNumber(1)
                .publicationYear(2006)
                .build(),

            Book.builder()
                .title("Clean Architecture")
                .author("Robert Martin")
                .description("Princípios fundamentais de arquitetura e design de software limpo")
                .editionNumber(1)
                .publicationYear(2017)
                .build(),

            Book.builder()
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .description("Modelagem de software orientada a domínio e padrões estratégicos")
                .editionNumber(1)
                .publicationYear(2003)
                .build(),

            Book.builder()
                .title("Design Patterns")
                .author("Gang of Four")
                .description("Padrões de projeto clássicos e reutilizáveis em software orientado a objetos")
                .editionNumber(1)
                .publicationYear(1994)
                .build(),

            Book.builder()
                .title("Building Microservices")
                .author("Sam Newman")
                .description("Guia prático para projetar e implementar sistemas de microservices")
                .editionNumber(2)
                .publicationYear(2021)
                .build(),

            Book.builder()
                .title("Microservices Patterns")
                .author("Chris Richardson")
                .description("Padrões e melhores práticas para arquitetura de microservices")
                .editionNumber(1)
                .publicationYear(2018)
                .build(),

            Book.builder()
                .title("The Phoenix Project")
                .author("Gene Kim")
                .description("Romance sobre DevOps, IT e ajuda ao negócio a vencer")
                .editionNumber(1)
                .publicationYear(2013)
                .build(),

            Book.builder()
                .title("Kubernetes in Action")
                .author("Marko Luksa")
                .description("Guia completo para orquestração de containers com Kubernetes")
                .editionNumber(2)
                .publicationYear(2020)
                .build(),

            Book.builder()
                .title("Designing Data-Intensive Applications")
                .author("Martin Kleppmann")
                .description("Fundamentos de sistemas distribuídos e bancos de dados modernos")
                .editionNumber(1)
                .publicationYear(2017)
                .build(),

            Book.builder()
                .title("Database Internals")
                .author("Alex Petrov")
                .description("Estruturas de dados e algoritmos de sistemas de banco de dados")
                .editionNumber(1)
                .publicationYear(2019)
                .build(),

            Book.builder()
                .title("Clean Code")
                .author("Robert Martin")
                .description("Manual de boas práticas para escrever código limpo e manutenível")
                .editionNumber(1)
                .publicationYear(2008)
                .build(),

            Book.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .description("Técnicas para melhorar o design de código existente")
                .editionNumber(2)
                .publicationYear(2018)
                .build(),

            Book.builder()
                .title("Scrum: The Art of Doing Twice the Work in Half the Time")
                .author("Jeff Sutherland")
                .description("Guia prático sobre metodologia Scrum e gestão ágil de projetos")
                .editionNumber(1)
                .publicationYear(2014)
                .build(),

            Book.builder()
                .title("The Pragmatic Programmer")
                .author("Andrew Hunt")
                .description("Dicas práticas para se tornar um programador melhor e mais produtivo")
                .editionNumber(2)
                .publicationYear(2019)
                .build(),

            Book.builder()
                .title("Soft Skills: The Software Developer's Life Manual")
                .author("John Sonmez")
                .description("Habilidades interpessoais essenciais para desenvolvedores de software")
                .editionNumber(1)
                .publicationYear(2014)
                .build(),

            Book.builder()
                .title("The Web Application Hacker's Handbook")
                .author("Dafydd Stuttard")
                .description("Guia completo sobre segurança em aplicações web")
                .editionNumber(2)
                .publicationYear(2011)
                .build(),

            Book.builder()
                .title("Test Driven Development")
                .author("Kent Beck")
                .description("Desenvolvimento guiado por testes e práticas de TDD")
                .editionNumber(1)
                .publicationYear(2002)
                .build(),

            Book.builder()
                .title("Introduction to Algorithms")
                .author("Thomas Cormen")
                .description("Livro clássico sobre algoritmos e estruturas de dados")
                .editionNumber(3)
                .publicationYear(2009)
                .build()
        );

        bookRepository.saveAll(books);
        log.info("{} livros criados com sucesso!", books.size());
    }
}
