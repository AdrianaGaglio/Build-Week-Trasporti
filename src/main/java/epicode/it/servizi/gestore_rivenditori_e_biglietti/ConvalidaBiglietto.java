package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.mezzo.Mezzo;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConvalidaBiglietto {

    private EntityManager em;

    public ConvalidaBiglietto(EntityManager em) {
        this.em = em;
    }

    // Metodo per convalidare un biglietto
    public void convalida(Giornaliero biglietto, Mezzo mezzo) {
        GiornalieroDAO bigliettoDAO = new GiornalieroDAO(em);
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        if (biglietto != null && biglietto.isDaAttivare()) {
            em.getTransaction().begin();
            biglietto.setDaAttivare(false);
            biglietto.setScadenza(LocalDateTime.now().plusMinutes(90));
            biglietto.setMezzo(mezzo);
            mezzo.getBiglietti().add(biglietto);

            em.merge(mezzo);
            em.merge(biglietto);

            em.getTransaction().commit();
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
