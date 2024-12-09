package epicode.it.entities.biglietto;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Abbonamento extends Biglietto {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicy periodicy;

    @Column(nullable = false)
    private boolean attivo;

    @Column(nullable = false)
    private String tariffa;

    // RELAZIONE CON TESSERA
    // @ManyToOne
    // @JoinColumn(name = "tessera_id", referencedColumnName = "id", nullable = true)
    // private Tessera tessera;

    // RELAZIONE CON TRATTA
    // @ManyToOne
    // @JoinColumn(name = "tratta_id", referencedColumnName = "id", nullable = true) // Foreign key verso Tratta
    // private Tratta tratta;

}
