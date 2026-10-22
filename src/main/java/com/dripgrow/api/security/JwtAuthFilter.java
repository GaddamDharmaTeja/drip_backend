package com.dripgrow.api.security;

import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.repository.AdminUserRepository;
import com.dripgrow.api.repository.CustomerRepository;
import com.dripgrow.api.security.StaffRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;
    private final CustomerRepository customerRepository;

    public JwtAuthFilter(JwtService jwtService, AdminUserRepository adminUserRepository, CustomerRepository customerRepository) {
        this.jwtService = jwtService;
        this.adminUserRepository = adminUserRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                boolean isCustomer = username.startsWith("customer:");
                AdminUser adminUser = isCustomer ? null : adminUserRepository.findByUsername(username).orElse(null);
                boolean userExists = isCustomer
                        ? customerRepository.existsById(username.substring("customer:".length()))
                        : adminUser != null && adminUser.isActive();
                if (userExists && jwtService.isValid(token, username)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority(isCustomer ? "ROLE_CUSTOMER" : "ROLE_" + StaffRole.from(adminUser.getRole()).name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
