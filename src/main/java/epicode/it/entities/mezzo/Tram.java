package epicode.it.entities.mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Tram", query = "SELECT a FROM Tram a")
public class Tram extends Mezzo{
    public Tram() {
        setCapienza(75);
    }

    @Override
    public String toString() {
        return "Tram{"+ getManutenzioni() +"}";
    }
}
