package epicode.it.entities.percorrenza;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
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

    public Collection<Object> getMezzi() {
        return List.of();
    }

    public void setTratta(Tratta tratta) {
    }

    public void setMezzi(List<Mezzo> mezzi) {
    }

    public Object getDurata_effettiva() {
        return null;
    }

    public void setDurata_effettiva(LocalTime tempoEffettivo) {
    }

    public void setData(LocalDate data) {
    }

    public Object getId() {
        return null;
    }
}