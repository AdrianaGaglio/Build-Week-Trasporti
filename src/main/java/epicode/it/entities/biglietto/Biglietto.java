package epicode.it.entities.biglietto;

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
@NamedQuery(name="Trova_tutto_Biglietto", query="SELECT a FROM Biglietto a")
@Table(name = "biglietti")
public abstract class Biglietto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String codice = "B-" + StringGenerator.random(10);

    @Column
    private LocalDateTime scadenza;

    private LocalDateTime emissione = LocalDateTime.now();

    @ManyToOne
    private Rivenditore rivenditore;

    @ManyToOne
    private Mezzo mezzo;

    private boolean daAttivare = true;

    @ManyToOne
    private Utente utente;

    // Getter e Setter per emissione (data di emissione)
    public LocalDateTime getEmissione() {
        return emissione;
    }

    public void setEmissione(LocalDateTime emissione) {
        this.emissione = emissione;
    }

    // Getter e Setter per scadenza (data di scadenza)
    public LocalDateTime getScadenza() {
        return scadenza;
    }

    public void setScadenza(LocalDateTime scadenza) {
        this.scadenza = scadenza;
    }

    // Getter e Setter per utente
    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public boolean isDaAttivare() {
        return daAttivare;
    }

    public void setDaAttivare(boolean daAttivare) {
        this.daAttivare = daAttivare;
    }
}
