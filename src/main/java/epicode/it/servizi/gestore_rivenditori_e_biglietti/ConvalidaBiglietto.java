package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.mezzo.Mezzo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConvalidaBiglietto {

    // Metodo per convalidare un biglietto
    public void convalida(Biglietto biglietto) {
        if (biglietto != null && biglietto.isDaAttivare()) {
            biglietto.setDaAttivare(false);
            biglietto.setScadenza(LocalDateTime.now().plusMinutes(90));
            System.out.println("Biglietto convalidato con successo.");
        } else {
            System.out.println("Errore: Biglietto non valido o già convalidato.");
        }
    }

    // Metodo per ottenere i biglietti convalidati su un determinato mezzo
    public List<Biglietto> bigliettiConvalidatiPerMezzo(Mezzo mezzo) {
        List<Biglietto> bigliettiConvalidati = new ArrayList<>();

        if (mezzo != null && mezzo.getBiglietti() != null) {
            for (Biglietto biglietto : mezzo.getBiglietti()) {
                if (!biglietto.isDaAttivare() && biglietto.getScadenza().isAfter(LocalDateTime.now())) {
                    bigliettiConvalidati.add(biglietto);
                }
            }
        } else {
            System.out.println("Nessun biglietto trovato per il mezzo specificato.");
        }
        return bigliettiConvalidati;
    }
}
