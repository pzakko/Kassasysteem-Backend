package nl.fontys.kassasysteem.kassa_systeem_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.enums.Rol;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.Product;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.User;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.ProductRepository;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.UserRepository;
import nl.fontys.kassasysteem.kassa_systeem_backend.config.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtTokenUtil jwtTokenUtil;

    private String jwtToken;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .username("admin")
                .password("test123")
                .role(Rol.ADMIN)
                .build();
        userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("test123")
                .roles("ADMIN")
                .build();
        jwtToken = jwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductDto dto = ProductDto.builder()
                .naam("Nieuw product")
                .prijs(BigDecimal.valueOf(29.99))
                .voorraad(20)
                .categorie("Broeken")
                .beschrijving("Mooie broek")
                .afbeelding("broek.jpg")
                .build();

        mockMvc.perform(post("/api/producten")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("Nieuw product"))
                .andExpect(jsonPath("$.prijs").value(29.99))
                .andExpect(jsonPath("$.voorraad").value(20));
    }


    @Test
    void testGetAllProducts_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/producten")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testCreateProduct_andFetchById() throws Exception {
        ProductDto dto = ProductDto.builder()
                .naam("Testproduct")
                .prijs(BigDecimal.valueOf(19.99))
                .voorraad(10)
                .categorie("Shirts")
                .beschrijving("Beschrijving")
                .afbeelding("link.jpg")
                .build();

        String response = mockMvc.perform(post("/api/producten")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        mockMvc.perform(get("/api/producten/" + created.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("Testproduct"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product saved = productRepository.save(Product.builder()
                .naam("Oud")
                .prijs(BigDecimal.valueOf(10))
                .voorraad(5)
                .build());

        ProductDto updateDto = ProductDto.builder()
                .naam("Nieuw")
                .prijs(BigDecimal.valueOf(20))
                .voorraad(15)
                .build();

        mockMvc.perform(put("/api/producten/" + saved.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam").value("Nieuw"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product saved = productRepository.save(Product.builder()
                .naam("Verwijderen")
                .prijs(BigDecimal.valueOf(5))
                .voorraad(2)
                .build());

        mockMvc.perform(delete("/api/producten/" + saved.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testZoeken() throws Exception {
        productRepository.save(Product.builder().naam("Blauwe jas").prijs(BigDecimal.TEN).voorraad(3).build());

        mockMvc.perform(get("/api/producten/zoeken")
                        .param("q", "jas")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].naam").value("Blauwe jas"));
    }

    @Test
    void testFilteren() throws Exception {
        productRepository.save(Product.builder().naam("Hemd").categorie("Overhemd").prijs(BigDecimal.TEN).voorraad(1).build());

        mockMvc.perform(get("/api/producten/filter")
                        .param("categorie", "Overhemd")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categorie").value("Overhemd"));
    }

    @Test
    void testZoekenEnFilteren() throws Exception {
        productRepository.save(Product.builder().naam("Zomerjas").categorie("Jassen").prijs(BigDecimal.TEN).voorraad(1).build());

        mockMvc.perform(get("/api/producten/zoeken-en-filteren")
                        .param("q", "Zomer")
                        .param("categorie", "Jassen")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].naam").value("Zomerjas"));
    }
}
