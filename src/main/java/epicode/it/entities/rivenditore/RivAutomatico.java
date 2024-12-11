package epicode.it.entities.rivenditore;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rivenditori_automatici")
@NamedQuery(name = "Trova_tutto_RivAutomatico", query = "SELECT a FROM RivAutomatico a")
public class RivAutomatico extends Rivenditore  {

    private boolean attivo;

    @Override
    public String toString() {
        return "\nRivenditoreAutomatico{" +
                "id= " + getId() + ", " +
                "attivo= " + attivo  +
                "}";
    }
}