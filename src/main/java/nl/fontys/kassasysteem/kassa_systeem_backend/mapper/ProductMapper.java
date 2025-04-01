package nl.fontys.kassasysteem.kassa_systeem_backend.mapper;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.model.Product;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        if (product == null) return null;

        return ProductDto.builder()
                .id(product.getId())
                .naam(product.getNaam())
                .prijs(product.getPrijs())
                .voorraad(product.getVoorraad())
                .categorie(product.getCategorie())
                .beschrijving(product.getBeschrijving())
                .afbeelding(product.getAfbeelding())
                .toegevoegdOp(product.getToegevoegdOp())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .id(dto.getId())
                .naam(dto.getNaam())
                .prijs(dto.getPrijs())
                .voorraad(dto.getVoorraad())
                .categorie(dto.getCategorie())
                .beschrijving(dto.getBeschrijving())
                .afbeelding(dto.getAfbeelding())
                .toegevoegdOp(dto.getToegevoegdOp())
                .build();
    }
}
