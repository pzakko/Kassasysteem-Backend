package nl.fontys.kassasysteem.kassa_systeem_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private int id;
    private String naam;
    private BigDecimal prijs;
    private int voorraad;
    private String categorie;
    private String beschrijving;
    private String afbeelding;
    private LocalDate toegevoegdOp = LocalDate.now();

}