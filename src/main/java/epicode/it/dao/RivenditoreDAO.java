package epicode.it.dao;

import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.utilities.StringGenerator;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class RivenditoreDAO {
    private EntityManager em;

    public void save(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public Rivenditore findById(Long id) {
        return em.find(Rivenditore.class, id);
    }

    public List<Rivenditore> findAll() {
        return em.createNamedQuery("Trova_tutto_Rivenditore", Rivenditore.class).getResultList();
    }


    public void update(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }

    //  aggiungi  biglietto
    public void creaGiornaliero(Rivenditore r, Tratta tratta) {
        Giornaliero biglietto = new Giornaliero();
        biglietto.setDaAttivare(true);
        biglietto.setTratta(tratta);
        biglietto.setScadenza(LocalDateTime.now().plus(Duration.ofMinutes(90)));
        biglietto.setRivenditore(r);
        r.getBiglietti().add(biglietto);
        update(r);
        BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
        bigliettoDAO.save(biglietto);
    }

    public void creaAbbonamento(Rivenditore r, Periodicy periodicy, Utente utente) {
        if (utente.getTessera() != null) {
            if (utente.getTessera().getAbbonamenti().getLast().getScadenza().isBefore(LocalDateTime.now())) {
                utente.getTessera().getAbbonamenti().getLast().setAttivo(false);
                Abbonamento a = new Abbonamento();
                a.setPeriodicy(periodicy);
                a.setScadenza(LocalDateTime.now().plusDays(switch (periodicy) {
                    case annuale -> 365;
                    case bimestrale -> 60;
                    case mensile -> 30;
                    case settimanale -> 7;
                    case trimestrale -> 90;
                }));
                a.setAttivo(true);
                a.setTessera(utente.getTessera());
                utente.getTessera().getAbbonamenti().add(a);
                r.getBiglietti().add(a);
                a.setRivenditore(r);
                update(r);
                BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
                bigliettoDAO.save(a);
            } else {
                System.out.println("Hai già un abbonamento attivo!");
            }
        } else {
            System.out.println("Devi prima creare la tessera per fare l' abbonaento!");
        }
    }

    // rimuovi  biglietto
//    public void removeBiglietto(Biglietto biglietto) {
//        biglietti.remove(biglietto);
//        biglietto.setRivenditore(null);
//        totaleBigliettiVenduti--;
//    }

}