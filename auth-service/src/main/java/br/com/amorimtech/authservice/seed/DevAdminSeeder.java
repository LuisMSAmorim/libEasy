package br.com.amorimtech.authservice.seed;

import br.com.amorimtech.authservice.model.Role;
import br.com.amorimtech.authservice.model.User;
import br.com.amorimtech.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DevAdminSeeder implements CommandLineRunner {
    private final UserRepository repo;
    private final PasswordEncoder enc;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            log.info("Usuários já existem no banco. Pulando seed.");
            return;
        }

        log.info("Iniciando seed de usuários...");

        List<User> users = List.of(
            User.builder()
                .name("Admin LibEasy")
                .email("admin@libeasy.local")
                .password(enc.encode("admin123"))
                .role(Role.ADMIN)
                .build(),

            User.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .password(enc.encode("senha123"))
                .role(Role.USER)
                .build(),

            User.builder()
                .name("Maria Santos")
                .email("maria.santos@email.com")
                .password(enc.encode("senha123"))
                .role(Role.USER)
                .build(),

            User.builder()
                .name("Pedro Oliveira")
                .email("pedro.oliveira@email.com")
                .password(enc.encode("senha123"))
                .role(Role.USER)
                .build(),

            User.builder()
                .name("Ana Costa")
                .email("ana.costa@email.com")
                .password(enc.encode("senha123"))
                .role(Role.USER)
                .build(),

            User.builder()
                .name("Carlos Ferreira")
                .email("carlos.ferreira@email.com")
                .password(enc.encode("senha123"))
                .role(Role.USER)
                .build()
        );

        repo.saveAll(users);
        log.info("{} usuários criados com sucesso!", users.size());
        log.info("Admin: admin@libeasy.local / admin123");
        log.info("Users: {email}@email.com / senha123");
    }
}
