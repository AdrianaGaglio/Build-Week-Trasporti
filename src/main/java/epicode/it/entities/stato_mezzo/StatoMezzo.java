package epicode.it.entities.stato_mezzo;

import epicode.it.entities.mezzo.Mezzo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_StatoMezzo", query = "SELECT a FROM StatoMezzo a")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "stato_mezzo")
public abstract class StatoMezzo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDate dataInizio;
    private LocalDate dataFine;

   // private Mezzo mezzo;

}
