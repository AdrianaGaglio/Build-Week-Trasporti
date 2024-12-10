package epicode.it.entities.mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Autobus", query = "SELECT a FROM Autobus a")
public class Autobus extends Mezzo{
    public Autobus() {
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
