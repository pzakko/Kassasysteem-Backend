package nl.fontys.kassasysteem.kassa_systeem_backend.service;

import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User gebruiker = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden"));

        // Voeg "ROLE_" toe voor Spring Security
        String role = "ROLE_" + gebruiker.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                gebruiker.getUsername(),
                gebruiker.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
