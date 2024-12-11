package epicode.it.entities.stato_mezzo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@NamedQuery(name = "Trova_tutto_Manutenzione", query = "SELECT a FROM Manutenzione a")
@NamedQuery(name="cercaSeInManutenzione", query = "SELECT s FROM Manutenzione s WHERE s.mezzo = :mezzo AND dataFine > :data OR dataFine IS NULL")
@NamedQuery(name="manutenzioniPerMezzo", query="SELECT s FROM Manutenzione s WHERE s.mezzo = :mezzo")
public class Manutenzione  extends  StatoMezzo{

    private String descrizione;

    @Override
    public String toString() {
        return "Manutenzione{" +
                "id=" + this.getId() +
                ", data_inizio=" + this.getDataInizio() +
                ", data_fine=" + this.getDataFine() +
                "descrizione='" + descrizione + '\'' +
                '}';
    }
}