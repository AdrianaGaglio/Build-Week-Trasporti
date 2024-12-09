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

    private String codice = "C-" + StringGenerator.random(10);

    private LocalDate validita;

//    @OneToMany(mappedBy = "tessera")
//    private List<Abbonamento> abbonamenti;


}