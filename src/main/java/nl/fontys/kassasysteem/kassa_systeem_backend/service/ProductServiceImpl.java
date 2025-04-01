package nl.fontys.kassasysteem.kassa_systeem_backend.service;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.mapper.ProductMapper;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.Product;
import nl.fontys.kassasysteem.kassa_systeem_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ProductDto> getAll() {
        return repo.findAll().stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public ProductDto getById(int id) {
        return repo.findById(id).map(ProductMapper::toDto).orElse(null);
    }

    @Override
    public ProductDto save(ProductDto dto) {
        Product entity = ProductMapper.toEntity(dto);
        return ProductMapper.toDto(repo.save(entity));
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }

    @Override
    public List<ProductDto> searchByNaam(String naam) {
        return repo.findByNaamContainingIgnoreCase(naam).stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public List<ProductDto> filterByCategorie(String categorie) {
        return repo.findByCategorieIgnoreCase(categorie).stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public List<ProductDto> searchAndFilter(String naam, String categorie) {
        return repo.findByNaamContainingIgnoreCaseAndCategorieIgnoreCase(naam, categorie)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}
