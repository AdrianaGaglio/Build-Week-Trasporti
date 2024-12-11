package epicode.it.entities.percorrenza;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @OneToOne
    private Mezzo mezzo;

    private LocalTime durata_effettiva;

    private LocalDateTime data;

    public Object getId() {
        return null;
    }
}