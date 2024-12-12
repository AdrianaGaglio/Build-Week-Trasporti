package epicode.it.entities.stato_mezzo;


import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@NamedQuery(name="cercaSeInServizio", query = "SELECT s FROM Servizio s WHERE s.mezzo = :mezzo AND (dataFine > :data OR dataFine IS NULL)")
@NamedQuery(name="serviziPerMezzo", query ="SELECT s FROM Servizio s WHERE s.mezzo = :mezzo")
public class Servizio extends StatoMezzo {
    @ManyToOne
    @JoinColumn(name="tratta_id")
    private Tratta tratta;

    @Override
    public String toString() {
        return "Servizio{" +
                "id=" + this.getId() +
                ", tratta=" + tratta +
                ", data_inizio=" + this.getDataInizio() +
                ", data_fine=" + this.getDataFine() +
                ", mezzo=" + this.getMezzo().getClass().getSimpleName() + " " + this.getMezzo().getId() +
                '}';
    }
}
