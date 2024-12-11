package epicode.it.entities.biglietto;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Giornaliero extends Biglietto {

    @Column(name="da_attivare")
    private boolean daAttivare;

    // RELAZIONE CON MEZZO
    @ManyToOne
    @JoinColumn(name = "mezzo_id", referencedColumnName = "id", nullable = true)
    private Mezzo mezzo;

    // RELAZIONE CON TRATTA
     @ManyToOne
     private Tratta tratta;

}
