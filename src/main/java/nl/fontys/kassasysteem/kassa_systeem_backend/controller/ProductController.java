package nl.fontys.kassasysteem.kassa_systeem_backend.controller;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producten")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ProductDto create(@RequestBody ProductDto dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable int id, @RequestBody ProductDto dto) {
        dto.setId(id);
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
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
