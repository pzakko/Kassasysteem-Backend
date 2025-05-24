package nl.fontys.kassasysteem.kassa_systeem_backend.controller;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.AuthRequest;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.AuthResponse;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import nl.fontys.kassasysteem.kassa_systeem_backend.config.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔐 LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtTokenUtil.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Ongeldige inloggegevens");
        }
    }

    // 🧾 REGISTER ENDPOINT
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Gebruikersnaam bestaat al");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(newUser);

        return ResponseEntity.ok("✅ Gebruiker succesvol aangemaakt");
    }
}
