package epicode.it.entities.mezzo;

import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.stato_mezzo.Manutenzione;
import epicode.it.entities.stato_mezzo.Servizio;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Mezzo", query = "SELECT a FROM Mezzo a")
@Table(name = "mezzi")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Mezzo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer capienza;

    @OneToMany(mappedBy = "mezzo")
   private List<Manutenzione> manutenzioni = new ArrayList<>();

    @OneToMany(mappedBy = "mezzo")
    private List<Servizio> servizi = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Stato stato;

    @ManyToMany(mappedBy = "mezzi")
    private List<Percorrenza> percorrenzae = new ArrayList<>();

}
