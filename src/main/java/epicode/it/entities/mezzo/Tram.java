package epicode.it.entities.mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("TRAM")
@NamedQuery(name = "Trova_tutto_Tram", query = "SELECT a FROM Tram a")
public class Tram extends Mezzo{

    public Tram() {

    }

    public Tram(int codice) {
        setCodice(codice);
        setCapienza(75);
    }

    @Override
    public String toString() {
        return "Tram{" +
                "id=" + this.getId() +
                ", capienza=" + this.getCapienza() +
                ", stato=" + this.getStato() +
                '}';
    }


}
