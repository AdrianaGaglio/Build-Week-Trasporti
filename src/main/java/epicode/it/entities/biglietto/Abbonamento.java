package epicode.it.entities.biglietto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("ABBONAMENTO")
@NamedQuery(name = "perTessera", query = "SELECT a FROM Abbonamento a WHERE a.tessera = :tessera ORDER BY a.id ASC")
@Table(name = "abbonamenti")
public class Abbonamento extends Biglietto {

    @Enumerated(EnumType.STRING)
    private Periodicy periodicy;

    @Column
    private boolean attivo;

    @Column
    private String tariffa;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tessera_id")
    private Tessera tessera;

    @Override
    public String toString() {
        return "Abbonamento{" +
                "id= " + getId() +
                ", codice= " + getCodice() +
                ", emissione= " + getEmissione() +
                ", periodicy= " + periodicy +
                ", attivo= " + attivo +
                ", scadenza= " + getScadenza() +
                ", utenteId= " + (getTessera().getUtente() != null ? getTessera().getUtente().getId() : null) +
                '}';
    }
}
