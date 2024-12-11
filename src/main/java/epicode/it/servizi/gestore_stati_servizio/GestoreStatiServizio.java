package epicode.it.servizi.gestore_stati_servizio;

import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.stato_mezzo.Manutenzione;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

public class GestoreStatiServizio {

    private EntityManager em;

    public GestoreStatiServizio(EntityManager em) {
        this.em = em;
    }

    public void aggiungiServizio(Mezzo m, LocalDate inizio, Tratta tratta) {
        ServizioDAO servizioDAO = new ServizioDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        Servizio foundS = servizioDAO.cercaSeInServizio(m, inizio);
        Manutenzione foundM = manutenzioneDAO.cercaSeInManutenzione(m, inizio);
        if (foundS == null) {
            em.getTransaction().begin();
            if (foundM != null) {
                foundM.setDataFine(inizio.minusDays(1));
                em.merge(foundM);
            }
            Servizio servizio = new Servizio();
            servizio.setDataInizio(inizio);
            servizio.setMezzo(m);
            servizio.setTratta(tratta);
            m.setStato(Stato.IN_SERVIZIO);
            m.getServizi().add(servizio);
            em.persist(servizio);
            em.merge(m);
            em.getTransaction().commit();
            System.out.println("Servizio assegnato correttamente per " + m);
        } else {
            System.out.println("Il mezzo è già in servizio: " + m);
        }
    }

    public void aggiungiManutenzione(Mezzo m, LocalDate inizio, String descrizione) {
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        ServizioDAO servizioDAO = new ServizioDAO(em);
        Manutenzione foundM = manutenzioneDAO.cercaSeInManutenzione(m, inizio);
        Servizio foundS = servizioDAO.cercaSeInServizio(m, inizio);
        if (foundM == null) {
            em.getTransaction().begin();
            if (foundS != null) {
                foundS.setDataFine(LocalDate.now());
                em.merge(foundS);
            }
            Manutenzione manutenzione = new Manutenzione();
            manutenzione.setDataInizio(inizio);
            manutenzione.setDataFine(null);
            manutenzione.setDescrizione(descrizione);
            manutenzione.setMezzo(m);
            em.persist(manutenzione);
            m.setStato(Stato.IN_MANUTENZIONE);
            m.getManutenzioni().add(manutenzione);
            em.merge(m);
            em.getTransaction().commit();
            System.out.println("Manutenzione inserita per " + m);
        }
    }

    public void spostaInDeposito(Mezzo m) {
        ServizioDAO servizioDAO = new ServizioDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        Servizio foundS = servizioDAO.cercaSeInServizio(m, LocalDate.now());
        Manutenzione foundM = manutenzioneDAO.cercaSeInManutenzione(m, LocalDate.now());
        em.getTransaction().begin();

        m.setStato(Stato.DEPOSITO);
        em.merge(m);

        if (foundS != null) {
            foundS.setDataFine(LocalDate.now());
            em.merge(foundS);
        }

        if (foundM != null) {
            foundM.setDataFine(LocalDate.now());
            em.merge(foundM);
        }

        em.getTransaction().commit();
    }
}
