package epicode.it.servizi.gestore_controllore;

import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.tratta.Tratta;
import java.time.LocalDateTime;

public class Controllore {

    public void controlloBiglietto(Biglietto biglietto, Tratta tratta) {
        LocalDateTime now = LocalDateTime.now();
        if (biglietto.getScadenza().isBefore(now)) {
            throw new IllegalStateException("Biglietto scaduto");
        }
    }

    public void controlloAbbonamento(Abbonamento abbonamento, Tratta tratta) {
        LocalDateTime now = LocalDateTime.now();
        if (abbonamento.getScadenza().isBefore(now)) {
            throw new IllegalStateException("Abbonamento scaduto");
        }

        if (!abbonamento.isAttivo()) {
            throw new IllegalStateException("Abbonamento non attivo");
        }
    }
}