package epicode.it.entities.stato_mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Manutenzione", query = "SELECT a FROM Manutenzione a")
public class Manutenzione  extends  StatoMezzo{

    private String descrizione;


}
