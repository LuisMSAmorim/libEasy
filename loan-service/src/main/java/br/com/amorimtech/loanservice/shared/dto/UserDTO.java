package br.com.amorimtech.loanservice.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String role;  // "USER" or "ADMIN"
    private String name;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean hasRole(String requiredRole) {
        return requiredRole.equals(role);
    }
}

