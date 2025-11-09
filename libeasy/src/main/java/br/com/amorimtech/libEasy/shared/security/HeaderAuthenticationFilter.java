package br.com.amorimtech.libEasy.shared.security;

import br.com.amorimtech.libEasy.shared.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_NAME = "X-User-Name";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);
        String userEmail = request.getHeader(HEADER_USER_EMAIL);
        String userRole = request.getHeader(HEADER_USER_ROLE);
        String userName = request.getHeader(HEADER_USER_NAME);

        if (userId != null && userEmail != null && userRole != null) {
            try {
                UserDTO user = new UserDTO(
                        Long.parseLong(userId),
                        userEmail,
                        userRole,
                        userName != null ? userName : ""
                );

                // Create authorities from role
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + userRole)
                );

                var authToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authentication set from headers for user: {} (role: {})", userEmail, userRole);
            } catch (NumberFormatException e) {
                log.error("Invalid user ID in header: {}", userId);
            }
        }

        chain.doFilter(request, response);
    }
}
