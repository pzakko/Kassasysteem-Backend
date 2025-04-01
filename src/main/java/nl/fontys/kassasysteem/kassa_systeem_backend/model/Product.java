package nl.fontys.kassasysteem.kassa_systeem_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Producten") // Expliciet tabelnaam opgeven
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(nullable = false)
    private String naam;

    @Column(nullable = false)
    private BigDecimal prijs;

    @Column(nullable = false)
    private int voorraad;

    @Column(nullable = true)
    private String categorie;

    @Column(nullable = true)
    private String beschrijving;

    @Column(nullable = true)
    private String afbeelding;

    @Column(name = "toegevoegd_op")
    private LocalDate toegevoegdOp = LocalDate.now();
}