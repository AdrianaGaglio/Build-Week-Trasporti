package epicode.it.entities.percorrenza;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@NamedQuery(name = "findAll_Percorrenza", query = "SELECT a FROM Percorrenza a")
@NamedQuery(name="trovaPerMezzo", query ="SELECT p FROM Percorrenza p WHERE p.mezzo = :mezzo")
@NamedQuery(name="trovaPerTratta", query ="SELECT p FROM Percorrenza p WHERE p.tratta = :tratta")
@NamedQuery(name="perMezzoTratta", query = "SELECT p FROM Percorrenza p WHERE p.tratta = :tratta AND p.mezzo = :mezzo")
public class Percorrenza {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tratta_id")
    private Tratta tratta;

    @ManyToOne
    private Mezzo mezzo;

    private LocalTime durata_effettiva;

    private LocalDateTime data;


}