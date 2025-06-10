package nl.fontys.kassasysteem.kassa_systeem_backend.controller;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.AuthRequest;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.AuthResponse;
import nl.fontys.kassasysteem.kassa_systeem_backend.enums.Rol;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import nl.fontys.kassasysteem.kassa_systeem_backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("LOGIN voor: " + request.getUsername()); // 👈 hier!
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Ongeldige gebruikersnaam of wachtwoord");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Gebruikersnaam bestaat al");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Rol.valueOf(request.getRole())) // ✅ Dit gebruikt de Enum correct
                .build();


        userRepository.save(newUser);

        return ResponseEntity.ok("✅ Gebruiker succesvol geregistreerd");
    }
}
