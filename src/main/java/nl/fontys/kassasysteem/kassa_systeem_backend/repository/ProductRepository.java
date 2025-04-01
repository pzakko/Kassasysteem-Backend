package nl.fontys.kassasysteem.kassa_systeem_backend.repository;

import nl.fontys.kassasysteem.kassa_systeem_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 🔍 Zoek op naam (deel van de naam, niet hoofdlettergevoelig)
    List<Product> findByNaamContainingIgnoreCase(String naam);

    // 🗂️ Filter op categorie (exacte match)
    List<Product> findByCategorieIgnoreCase(String categorie);

    // 👇 combineren (naam + categorie)
    List<Product> findByNaamContainingIgnoreCaseAndCategorieIgnoreCase(String naam, String categorie);
}
