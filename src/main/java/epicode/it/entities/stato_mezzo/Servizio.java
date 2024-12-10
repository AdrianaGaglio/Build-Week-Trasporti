package epicode.it.entities.stato_mezzo;


import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@NamedQuery(name="cercaSeInServizio", query = "SELECT s FROM Servizio s WHERE s.mezzo = :mezzo AND dataFine > :data AND dataFine IS NOT NULL")
public class Servizio extends StatoMezzo {
    @ManyToOne
    @JoinColumn(name="tratta_id")
    private Tratta tratta;

    @Override
    public String toString() {
        return "Servizio{" +
                "tratta=" + tratta +
                ", data_inizio=" + this.getDataInizio() +
                ", data_fine=" + this.getDataFine() +
                ", mezzo=" + this.getMezzo() +
                '}';
    }
}
