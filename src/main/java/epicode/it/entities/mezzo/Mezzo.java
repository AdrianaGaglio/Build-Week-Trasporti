package epicode.it.entities.mezzo;

import epicode.it.entities.stato_mezzo.Manutenzione;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Mezzo", query = "SELECT a FROM Mezzo a")
@Table(name = "mezzi")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Mezzo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer capienza;

    @OneToMany
   private List<Manutenzione> manutenzioni = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Stato stato;

}
