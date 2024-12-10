package epicode.it.entities.stato_mezzo;


import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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
