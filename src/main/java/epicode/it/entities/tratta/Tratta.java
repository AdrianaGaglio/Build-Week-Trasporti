package epicode.it.entities.tratta;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.stato_mezzo.Servizio;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "findAll_Tratta", query = "SELECT a FROM Tratta a")
@Table(name="tratte")
public class Tratta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String partenza;
    private String capolinea;
    private LocalTime durata;

    @OneToMany(mappedBy = "tratta")
    @ToString.Exclude
    private List<Servizio> servizi = new ArrayList<>();

    @OneToMany(mappedBy = "tratta")
    private List<Percorrenza> percorrenze = new ArrayList<>();
//
//    public Percorrenza percorriTratta(List<Mezzo> mezzi, LocalDate data) {
//        Percorrenza nuovaPercorrenza = new Percorrenza();
//        nuovaPercorrenza.setTratta(this);
//        nuovaPercorrenza.setMezzi(mezzi);
//        nuovaPercorrenza.setData(data);
//        nuovaPercorrenza.setDurata_effettiva(this.durata);
//        this.percorrenze.add(nuovaPercorrenza);
//        return nuovaPercorrenza;
//    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", partenza='" + partenza + '\'' +
                ", capolinea='" + capolinea + '\'' +
                ", durata=" + durata +
                // Evita di includere la lista completa dei servizi per prevenire cicli
                '}';
    }
}