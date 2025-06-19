package nl.fontys.kassasysteem.kassa_systeem_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.AuthRequest;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final String username = "testgebruiker";
    private final String wachtwoord = "wachtwoord123";
    private final String rol = "GEBRUIKER";

    @Test
    @Order(1)
    public void testRegisterNewUser_ReturnsOk() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(wachtwoord);
        request.setRole(rol);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("✅ Gebruiker succesvol geregistreerd")));
    }

    @Test
    @Order(2)
    public void testRegisterSameUser_ReturnsBadRequest() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(wachtwoord);
        request.setRole(rol);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("⚠️ Gebruikersnaam bestaat al")));
    }

    @Test
    @Order(3)
    public void testLoginValidUser_ReturnsJwtToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(wachtwoord);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @Order(4)
    public void testLoginInvalidPassword_ReturnsUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword("verkeerdWachtwoord");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("❌ Ongeldige gebruikersnaam of wachtwoord")));
    }

    @Test
    @Order(5)
    public void testLoginNonExistentUser_ReturnsUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("nietbestaand");
        request.setPassword("willekeurig");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("❌ Ongeldige gebruikersnaam of wachtwoord")));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
