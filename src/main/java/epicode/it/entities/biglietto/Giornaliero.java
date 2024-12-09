package epicode.it.entities.biglietto;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Giornaliero extends Biglietto{
    private boolean da_attivare;
    // Mezzo mezzo;
    // Tratta tratta;
}
