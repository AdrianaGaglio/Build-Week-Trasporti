package epicode.it.entities.biglietto;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Abbonamento extends Biglietto{
    private Periodicy periodicy;
    private boolean attivo;
    // private List<Tratta> tratte;
    private String tariffa;

    // @ManyToOne
    // Tessera tessera
    // Tratta tratta
}
