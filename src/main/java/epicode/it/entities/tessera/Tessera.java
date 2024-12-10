package epicode.it.entities.tessera;

import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.utilities.StringGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "findAll_Tessera", query = "SELECT a FROM Tessera a")
@Table(name="tessere")
public class Tessera {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String codice = "T-" + StringGenerator.random(10);

    private LocalDate validita;

    @OneToMany(mappedBy = "tessera")
    private List<Abbonamento> abbonamenti;

}