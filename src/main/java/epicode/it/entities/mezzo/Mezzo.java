package epicode.it.entities.mezzo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import epicode.it.entities.biglietto.Biglietto;
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
@NamedQuery(name="trovaPerNumeroLinea", query ="SELECT a FROM Mezzo a WHERE codice = :codice")
@NamedQuery(name="trovaMezzoPerStato", query ="SELECT a FROM Mezzo a WHERE stato = :stato ")
@Table(name = "mezzi")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_mezzo")
public abstract class Mezzo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer capienza;

    @OneToMany(mappedBy = "mezzo")
    private List<Manutenzione> manutenzioni = new ArrayList<>();

    @OneToMany(mappedBy = "mezzo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Servizio> servizi = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Stato stato;

    @OneToMany(mappedBy = "mezzo")
    private List<Percorrenza> percorrenze = new ArrayList<>();

    @OneToMany(mappedBy = "mezzo")
    private List<Biglietto> biglietti = new ArrayList<>();

    private Integer codice;

    public List<Biglietto> getBiglietti() {
        return biglietti;
    }
}
