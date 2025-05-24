package nl.fontys.kassasysteem.kassa_systeem_backend.service;

import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), java.util.List.of(new SimpleGrantedAuthority(user.getRole())));
    }
}
