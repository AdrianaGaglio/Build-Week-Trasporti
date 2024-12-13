package epicode.it.entities.biglietto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.utente.Utente;
import epicode.it.utilities.StringGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "Trova_tutto_Biglietto", query = "SELECT a FROM Biglietto a")

@Table(name = "biglietti")
public abstract class Biglietto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String codice = "B-" + StringGenerator.random(10);

    @Column
    private LocalDateTime scadenza;

    @Column(nullable = false)
    private LocalDateTime emissione = LocalDateTime.now();

    @ManyToOne
    @JsonBackReference
    private Rivenditore rivenditore;

    @ManyToOne
    private Mezzo mezzo;

    @Column(nullable = false)
    private boolean daAttivare = true;

    @ManyToOne
    private Utente utente;

    private String tipo;
}
