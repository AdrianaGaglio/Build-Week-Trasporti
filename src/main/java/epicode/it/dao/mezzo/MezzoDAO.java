package epicode.it.dao.mezzo;

import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class MezzoDAO {
    private EntityManager em;

    public void save(Mezzo oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public void saveAll(List<Mezzo> mezzi) {
        em.getTransaction().begin();
        for (Mezzo mezzo : mezzi) {
            em.persist(mezzo);
        }
        em.getTransaction().commit();
    }

    public Mezzo findById(Long id) {
        return em.find(Mezzo.class, id);
    }

    public List<Mezzo> findAll() {
        return em.createNamedQuery("Trova_tutto_Mezzo", Mezzo.class).getResultList();
    }

    public void update(Mezzo oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Mezzo oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }

    public void aggiungiServizio(Mezzo m, LocalDate inizio, LocalDate fine, Tratta tratta) {
        StatoMezzoDAO statoMezzoDAO = new StatoMezzoDAO(em);
        Servizio found = statoMezzoDAO.cercaSeInServizio(m, inizio);
        if(found == null) {
            em.getTransaction().begin();
            Servizio servizio = new Servizio();
            servizio.setDataInizio(inizio);
            servizio.setDataFine(fine);
            servizio.setMezzo(m);
            servizio.setTratta(tratta);
            em.persist(servizio);
            m.setStato(Stato.IN_SERVIZIO);
            m.getServizi().add(servizio);
            em.merge(m);
            em.getTransaction().commit();
            System.out.println("Servizio assegnato correttamente per " + m);
        } else {
            System.out.println("Il mezzo è già in servizio: " + found);
        }

    }


}