package epicode.it.entities.biglietto;

import epicode.it.utilities.StringGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public abstract class Biglietto {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String codice = "B-" + StringGenerator.random(10);
    private LocalDate scadenza;

    //@ManyToOne
    // Rivenditore rivenditore
}
