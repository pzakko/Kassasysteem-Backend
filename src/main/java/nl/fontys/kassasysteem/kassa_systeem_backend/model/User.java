package nl.fontys.kassasysteem.kassa_systeem_backend.model;
import jakarta.persistence.*;
import lombok.*;
import nl.fontys.kassasysteem.kassa_systeem_backend.enums.Rol;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gebruikers")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Rol role;
}
