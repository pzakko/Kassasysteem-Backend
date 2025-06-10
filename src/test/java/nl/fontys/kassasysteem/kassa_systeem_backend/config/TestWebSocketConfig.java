package nl.fontys.kassasysteem.kassa_systeem_backend.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@TestConfiguration
public class TestWebSocketConfig {

    @Bean
    @Primary  // ✅ maakt deze bean de standaard als er meerdere zijn
    public SimpMessagingTemplate simpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
}

