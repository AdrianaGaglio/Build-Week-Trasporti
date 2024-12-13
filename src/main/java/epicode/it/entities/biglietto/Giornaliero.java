package epicode.it.entities.biglietto;

import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("GIORNALIERO")
@NamedQuery(name = "Trova_tutto_Giornaliero", query = "SELECT a FROM Giornaliero a")
public class Giornaliero extends Biglietto {

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
