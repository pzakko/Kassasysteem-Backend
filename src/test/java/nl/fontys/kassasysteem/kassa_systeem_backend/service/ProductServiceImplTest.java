package nl.fontys.kassasysteem.kassa_systeem_backend.service;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.Product;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SimpMessagingTemplate berichtVerzender;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void voorbereiding() {
        product = Product.builder()
                .id(1)
                .naam("Testproduct")
                .prijs(new BigDecimal("10.00"))
                .voorraad(100)
                .categorie("Categorie A")
                .beschrijving("Beschrijving van testproduct")
                .afbeelding("test.jpg")
                .toegevoegdOp(LocalDate.now())
                .build();

        productDto = new ProductDto();
        productDto.setId(1);
        productDto.setNaam("Testproduct");
        productDto.setPrijs(new BigDecimal("10.00"));
        productDto.setVoorraad(100);
        productDto.setCategorie("Categorie A");
        productDto.setBeschrijving("Beschrijving van testproduct");
        productDto.setAfbeelding("test.jpg");
        productDto.setToegevoegdOp(LocalDate.now());
    }

    @Test
    void getAll_geeftLijstMetProducten() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDto> resultaat = productService.getAll();

        assertEquals(1, resultaat.size());
        assertEquals("Testproduct", resultaat.get(0).getNaam());
        verify(productRepository).findAll();
    }

    @Test
    void getAll_geeftLegeLijst() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductDto> resultaat = productService.getAll();

        assertTrue(resultaat.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void getById_vindtProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductDto resultaat = productService.getById(1);

        assertNotNull(resultaat);
        assertEquals("Testproduct", resultaat.getNaam());
        verify(productRepository).findById(1);
    }

    @Test
    void getById_geeftNullBijGeenResultaat() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        ProductDto resultaat = productService.getById(1);

        assertNull(resultaat);
        verify(productRepository).findById(1);
    }

    @Test
    void save_slaatProductOpEnStuurtBericht() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto resultaat = productService.save(productDto);

        assertNotNull(resultaat);
        assertEquals("Testproduct", resultaat.getNaam());
        verify(productRepository).save(any(Product.class));
        verify(berichtVerzender).convertAndSend(eq("/topic/products"), any(ProductDto.class));
    }

    @Test
    void delete_verwijdertProduct() {
        productService.delete(1);
        verify(productRepository).deleteById(1);
    }

    @Test
    void zoekenOpNaam_geeftResultaten() {
        when(productRepository.findByNaamContainingIgnoreCase("Test")).thenReturn(List.of(product));

        List<ProductDto> resultaat = productService.searchByNaam("Test");

        assertEquals(1, resultaat.size());
        assertEquals("Testproduct", resultaat.get(0).getNaam());
        verify(productRepository).findByNaamContainingIgnoreCase("Test");
    }

    @Test
    void zoekenOpNaam_geeftLegeLijst() {
        when(productRepository.findByNaamContainingIgnoreCase("Onbekend")).thenReturn(Collections.emptyList());

        List<ProductDto> resultaat = productService.searchByNaam("Onbekend");

        assertTrue(resultaat.isEmpty());
        verify(productRepository).findByNaamContainingIgnoreCase("Onbekend");
    }

    @Test
    void filterenOpCategorie_geeftResultaten() {
        when(productRepository.findByCategorieIgnoreCase("Categorie A")).thenReturn(List.of(product));

        List<ProductDto> resultaat = productService.filterByCategorie("Categorie A");

        assertEquals(1, resultaat.size());
        assertEquals("Categorie A", resultaat.get(0).getCategorie());
        verify(productRepository).findByCategorieIgnoreCase("Categorie A");
    }

    @Test
    void filterenOpCategorie_geeftLegeLijst() {
        when(productRepository.findByCategorieIgnoreCase("Onbekend")).thenReturn(Collections.emptyList());

        List<ProductDto> resultaat = productService.filterByCategorie("Onbekend");

        assertTrue(resultaat.isEmpty());
        verify(productRepository).findByCategorieIgnoreCase("Onbekend");
    }

    @Test
    void zoekenEnFilteren_geeftResultaten() {
        when(productRepository.findByNaamContainingIgnoreCaseAndCategorieIgnoreCase("Test", "Categorie A"))
                .thenReturn(List.of(product));

        List<ProductDto> resultaat = productService.searchAndFilter("Test", "Categorie A");

        assertEquals(1, resultaat.size());
        assertEquals("Testproduct", resultaat.get(0).getNaam());
        verify(productRepository).findByNaamContainingIgnoreCaseAndCategorieIgnoreCase("Test", "Categorie A");
    }

    @Test
    void zoekenEnFilteren_geeftLegeLijst() {
        when(productRepository.findByNaamContainingIgnoreCaseAndCategorieIgnoreCase("X", "Y"))
                .thenReturn(Collections.emptyList());

        List<ProductDto> resultaat = productService.searchAndFilter("X", "Y");

        assertTrue(resultaat.isEmpty());
        verify(productRepository).findByNaamContainingIgnoreCaseAndCategorieIgnoreCase("X", "Y");
    }
}
