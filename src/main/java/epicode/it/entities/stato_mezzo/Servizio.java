package epicode.it.entities.stato_mezzo;


import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Servizio extends StatoMezzo {
    @OneToOne
    private Tratta tratta;
}
