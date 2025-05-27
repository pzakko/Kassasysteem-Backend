package nl.fontys.kassasysteem.kassa_systeem_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductIntegratieTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ProductDto nieuwProduct;

    @BeforeEach
    void setUp() {
        nieuwProduct = new ProductDto();
        nieuwProduct.setNaam("WebSocketTest");
        nieuwProduct.setPrijs(BigDecimal.valueOf(19.99));
        nieuwProduct.setVoorraad(5);
        nieuwProduct.setCategorie("WebTest");
        nieuwProduct.setBeschrijving("Beschrijving van testproduct");
    }

    @Test
    void testFullCrudProduct() throws Exception {
        // CREATE
        MvcResult postResult = mockMvc.perform(post("/api/producten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nieuwProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("WebSocketTest"))
                .andReturn();

        ProductDto opgeslagenProduct = objectMapper.readValue(
                postResult.getResponse().getContentAsString(), ProductDto.class);

        // READ
        mockMvc.perform(get("/api/producten/" + opgeslagenProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voorraad").value(5));

        // UPDATE
        opgeslagenProduct.setPrijs(BigDecimal.valueOf(19.99));
        opgeslagenProduct.setVoorraad(3);

        mockMvc.perform(put("/api/producten/" + opgeslagenProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opgeslagenProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prijs").value(15.50));

        // DELETE
        mockMvc.perform(delete("/api/producten/" + opgeslagenProduct.getId()))
                .andExpect(status().isOk());

        // CHECK if deleted
        mockMvc.perform(get("/api/producten/" + opgeslagenProduct.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testWebSocketNotificationSimulation() throws Exception {
        // Simuleer dat WebSocket een bericht ontvangt bij create
        messagingTemplate.convertAndSend("/topic/producten", nieuwProduct);
        messagingTemplate.convertAndSend("/topic/verwijderd", 999);

        // Dit test geen echte WebSocket client, maar valideert dat de backend zonder fout verzendt.
        assertThat(nieuwProduct.getNaam()).isEqualTo("WebSocketTest");
    }
}
