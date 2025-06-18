package nl.fontys.kassasysteem.kassa_systeem_backend.controller;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.service.ProductService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producten")
public class ProductController {

    private final ProductService service;
    private final SimpMessagingTemplate messagingTemplate;

    public ProductController(ProductService service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    @PreAuthorize("hasRole('GEBRUIKER') or hasRole('ADMIN')")
    public List<ProductDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductDto create(@RequestBody ProductDto dto) {
        ProductDto nieuwProduct = service.save(dto);
        messagingTemplate.convertAndSend("/topic/producten", nieuwProduct); // 🔔 notify clients
        return nieuwProduct;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable int id, @RequestBody ProductDto dto) {
        dto.setId(id);
        ProductDto bijgewerkt = service.save(dto);
        messagingTemplate.convertAndSend("/topic/producten", bijgewerkt); // 🔔 notify clients
        return bijgewerkt;
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
        messagingTemplate.convertAndSend("/topic/verwijderd", id); // 🔔 notify clients
    }

    @GetMapping("/zoeken")
    public List<ProductDto> search(@RequestParam String q) {
        return service.searchByNaam(q);
    }

    @GetMapping("/filter")
    public List<ProductDto> filter(@RequestParam String categorie) {
        return service.filterByCategorie(categorie);
    }

    @GetMapping("/zoeken-en-filteren")
    public List<ProductDto> searchAndFilter(@RequestParam String q, @RequestParam String categorie) {
        return service.searchAndFilter(q, categorie);
    }
}
