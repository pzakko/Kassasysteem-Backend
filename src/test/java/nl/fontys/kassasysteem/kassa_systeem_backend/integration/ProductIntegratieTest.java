package nl.fontys.kassasysteem.kassa_systeem_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegratieTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProductToevoegenEnOphalen() throws Exception {
        ProductDto nieuwProduct = ProductDto.builder()
                .naam("IntegratieTestProduct")
                .prijs(BigDecimal.valueOf(19.99))
                .voorraad(10)
                .categorie("Test")
                .beschrijving("Testbeschrijving")
                .afbeelding("test.jpg")
                .build();

        // 1. POST: voeg product toe
        mockMvc.perform(post("/api/producten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nieuwProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("IntegratieTestProduct"));

        // 2. GET: controleer of het terugkomt
        mockMvc.perform(get("/api/producten"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].naam").value("IntegratieTestProduct"));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductDto nieuwProduct = ProductDto.builder()
                .naam("ProductOpvragen")
                .prijs(BigDecimal.valueOf(15.00))
                .voorraad(8)
                .categorie("Categorie")
                .beschrijving("Beschrijving")
                .afbeelding("img.jpg")
                .build();

        // Eerst: voeg product toe
        String response = mockMvc.perform(post("/api/producten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nieuwProduct)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductDto opgeslagen = objectMapper.readValue(response, ProductDto.class);

        // Dan: haal product op via id
        mockMvc.perform(get("/api/producten/" + opgeslagen.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("ProductOpvragen"));
    }

    @Test
    void testProductBijwerken() throws Exception {
        ProductDto origineel = ProductDto.builder()
                .naam("Oude Naam")
                .prijs(BigDecimal.valueOf(10))
                .voorraad(5)
                .categorie("Test")
                .beschrijving("Oud")
                .afbeelding("oud.jpg")
                .build();

        // Voeg toe
        String response = mockMvc.perform(post("/api/producten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(origineel)))
                .andReturn().getResponse().getContentAsString();

        ProductDto toegevoegd = objectMapper.readValue(response, ProductDto.class);

        // Wijzig
        toegevoegd.setNaam("Nieuwe Naam");

        mockMvc.perform(put("/api/producten/" + toegevoegd.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toegevoegd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("Nieuwe Naam"));
    }

    @Test
    void testProductVerwijderen() throws Exception {
        ProductDto testProduct = ProductDto.builder()
                .naam("Verwijder Mij")
                .prijs(BigDecimal.valueOf(12.34))
                .voorraad(2)
                .categorie("Delete")
                .beschrijving("Verwijderen")
                .afbeelding("img.png")
                .build();

        // Voeg toe
        String response = mockMvc.perform(post("/api/producten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andReturn().getResponse().getContentAsString();

        ProductDto opgeslagen = objectMapper.readValue(response, ProductDto.class);

        // Verwijder
        mockMvc.perform(delete("/api/producten/" + opgeslagen.getId()))
                .andExpect(status().isOk());

        // Controleer dat GET nu niets oplevert of null
        mockMvc.perform(get("/api/producten/" + opgeslagen.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
