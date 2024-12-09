package epicode.it.entities.biglietto;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Giornaliero extends Biglietto {

    @Column(name="da_attivare", nullable = false)
    private boolean daAttivare;

    // RELAZIONE CON MEZZO
    // @ManyToOne
    // @JoinColumn(name = "mezzo_id", referencedColumnName = "id", nullable = true) // Foreign key verso Mezzo
    // private Mezzo mezzo;

    // RELAZIONE CON TRATTA
    // @ManyToOne
    //@JoinColumn(name = "tratta_id", referencedColumnName = "id", nullable = true) // Foreign key verso Tratta
    // private Tratta tratta;

}
