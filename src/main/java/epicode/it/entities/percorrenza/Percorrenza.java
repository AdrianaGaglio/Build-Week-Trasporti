package epicode.it.entities.percorrenza;

import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

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

//    @ManyToMany
//    private Mezzo mezzo;

    private LocalTime durata_effettiva;

    private LocalDate data;
}