package br.com.amorimtech.libEasy.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
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
