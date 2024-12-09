package epicode.it.entities.rivenditore;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "rivenditori")
@NamedQuery(name = "Trova_tutto_Rivenditore", query = "SELECT a FROM Rivenditore a")
public abstract class Rivenditore {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    //List<Biglietto> biglietti = new ArrayList<>();

}