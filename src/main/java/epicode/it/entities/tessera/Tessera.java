package epicode.it.entities.tessera;

import epicode.it.utilities.StringGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@NamedQuery(name = "findAll_Tessera", query = "SELECT a FROM Tessera a")
@Table(name="tessere")
public class Tessera {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String codice = "T-" + StringGenerator.random(10);

<<<<<<< Updated upstream
    private LocalDate validita;

//    @OneToMany(mappedBy = "tessera")
//    private List<Abbonamento> abbonamenti;

=======
    private LocalDateTime validita;

    @OneToMany(mappedBy = "tessera")
    private List<Abbonamento> abbonamenti;

    @OneToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
>>>>>>> Stashed changes

}