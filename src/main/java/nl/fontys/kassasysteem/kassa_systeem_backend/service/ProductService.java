package nl.fontys.kassasysteem.kassa_systeem_backend.service;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAll();
    ProductDto getById(int id);
    ProductDto save(ProductDto dto);
    void delete(int id);

    List<ProductDto> searchByNaam(String naam);
    List<ProductDto> filterByCategorie(String categorie);
    List<ProductDto> searchAndFilter(String naam, String categorie);
}
