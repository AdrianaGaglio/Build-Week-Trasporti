package epicode.it.entities.biglietto;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Giornaliero", query = "SELECT a FROM Giornaliero a")

public class Giornaliero extends Biglietto {

    // RELAZIONE CON TRATTA
    @ManyToOne
    private Tratta tratta;

    @Override
    public String toString() {
        return "Giornaliero{" +
                "id=" + getId() +
                ", daAttivare=" + isDaAttivare() +
                ", mezzo=" + getMezzo() +
                ", tratta=" + tratta +
                '}';
    }
}
