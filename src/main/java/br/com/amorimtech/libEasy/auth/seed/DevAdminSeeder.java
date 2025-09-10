// br/com/amorimtech/libEasy/seed/DevAdminSeeder.java
package br.com.amorimtech.libEasy.auth.seed;

import br.com.amorimtech.libEasy.auth.model.Role;
import br.com.amorimtech.libEasy.auth.model.User;
import br.com.amorimtech.libEasy.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevAdminSeeder implements CommandLineRunner {
    private final UserRepository repo;
    private final PasswordEncoder enc;

    @Override
    public void run(String... args) {
        if (repo.existsByEmail("admin@libeasy.local")) return;
        repo.save(User.builder()
                .name("Admin (DEV)")
                .email("admin@libeasy.local")
                .password(enc.encode("admin123"))
                .role(Role.ADMIN)
                .build());
    }
}
