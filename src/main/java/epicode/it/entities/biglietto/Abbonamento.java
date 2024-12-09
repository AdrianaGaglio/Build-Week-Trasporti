package epicode.it.entities.biglietto;

import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="abbonamenti")
public class Abbonamento extends Biglietto {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicy periodicy;

    @Column(nullable = false)
    private boolean attivo;

    @Column(nullable = false)
    private String tariffa;

    // RELAZIONE CON TESSERA
     @ManyToOne
     @JoinColumn(name = "tessera_id")
     private Tessera tessera;

    // RELAZIONE CON TRATTA
     @ManyToMany
     @JoinColumn(name = "tratta_id") // Foreign key verso Tratta
     private List<Tratta> tratte = new ArrayList<>();

}
