package epicode.it.entities.mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("AUTOBUS")
@NamedQuery(name = "Trova_tutto_Autobus", query = "SELECT a FROM Autobus a")
public class Autobus extends Mezzo{

    public Autobus() {

    }

    public Autobus(int codice) {
        setCodice(codice);
        setCapienza(50);
    }

    @Override
    public String toString() {
        return "Autobus{" +
                "id=" + this.getId() +
                ", capienza=" + this.getCapienza() +
                ", stato=" + this.getStato() +
                '}';
    }


}
