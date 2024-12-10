package epicode.it.entities.tratta;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.stato_mezzo.Servizio;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Time;
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


}