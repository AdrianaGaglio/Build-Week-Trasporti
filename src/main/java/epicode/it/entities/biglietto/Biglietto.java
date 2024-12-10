package epicode.it.entities.biglietto;

import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.utilities.StringGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "biglietti")
public abstract class Biglietto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String codice = "B-" + StringGenerator.random(10);


    @Column(nullable = false)
    private LocalDateTime scadenza;




    @ManyToOne
    private Rivenditore rivenditore;

}
