package epicode.it.entities.rivenditore;

import epicode.it.entities.biglietto.Biglietto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "rivenditori")
@NamedQuery(name = "Trova_tutto_Rivenditore", query = "SELECT a FROM Rivenditore a")
public abstract class Rivenditore {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(mappedBy = "rivenditore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Biglietto> biglietti = new ArrayList<>();

    @Column(name = "biglietti_venduti", nullable = false)
    private int totaleBigliettiVenduti = 0;

    //  aggiungi  biglietto
    public void addBiglietto(Biglietto biglietto) {
        biglietti.add(biglietto);
        biglietto.setRivenditore(this);
        totaleBigliettiVenduti++;
    }

    // rimuovi  biglietto
    public void removeBiglietto(Biglietto biglietto) {
        biglietti.remove(biglietto);
        biglietto.setRivenditore(null);
        totaleBigliettiVenduti--;
    }
}
