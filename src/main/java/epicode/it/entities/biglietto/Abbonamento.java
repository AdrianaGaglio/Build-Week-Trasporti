package epicode.it.entities.biglietto;

import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("ABBONAMENTO")
@NamedQuery(name = "perTessera", query = "SELECT a FROM Abbonamento a WHERE a.tessera = :tessera ORDER BY a.id ASC")
public class Abbonamento extends Biglietto {

    @Enumerated(EnumType.STRING)
    private Periodicy periodicy;

    @Column
    private boolean attivo;

    @Column
    private String tariffa;

    @ManyToOne
    @JoinColumn(name = "tessera_id")
    private Tessera tessera;

    @Override
    public String toString() {
        return "Abbonamento{" +
                "id=" + getId() +
                ", periodicy=" + periodicy +
                ", attivo=" + attivo +
                ", tariffa='" + tariffa + '\'' +
                ", tessera=" + tessera +
                '}';
    }
}
