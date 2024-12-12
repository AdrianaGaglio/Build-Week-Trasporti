package epicode.it.dao.rivenditore;

import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

}

