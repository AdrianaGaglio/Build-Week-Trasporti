package epicode.it.entities.percorrenza;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "findAll_Percorrenza", query = "SELECT a FROM Percorrenza a")
public class Percorrenza {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tratta_id")
    private Tratta tratta;

    @ManyToMany
    private List<Mezzo> mezzi;

    private LocalTime durata_effettiva;

    private LocalDate data;

    public void setTratta(Tratta tratta) {
    }

    public void setData(LocalDate data) {
    }

    public void setMezzi(List<Mezzo> mezzi) {
    }

    public void setDurata_effettiva(LocalTime durata) {
    }

    public Object getId() {
        return null;
    }
}